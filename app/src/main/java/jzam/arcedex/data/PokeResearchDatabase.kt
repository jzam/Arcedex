package jzam.arcedex.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import jzam.arcedex.models.PokeResearch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
 * Set up the room database for the PokeResearch data. Ensures there's only 1 instance of the
 * database for the whole app. Populates database on creation.
 */
@Database(entities = arrayOf(PokeResearch::class), version = 1, exportSchema = false)
public abstract class PokeResearchDatabase : RoomDatabase() {

    abstract fun pokeResearchDao(): PokeResearchDao

    companion object {
        @Volatile
        private var INSTANCE: PokeResearchDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PokeResearchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PokeResearchDatabase::class.java,
                    "pokeresearch_database"
                )
                    .addCallback(PokeResearchDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class PokeResearchDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.pokeResearchDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(pokeResearchDao: PokeResearchDao) {
            if (pokeResearchDao.getCount() == 0) {
                val researchTasks = PokeResearchData.tasks
                for (task in researchTasks) {
                    pokeResearchDao.insert(task)
                }
            }
        }
    }
}