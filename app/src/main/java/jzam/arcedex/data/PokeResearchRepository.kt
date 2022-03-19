package jzam.arcedex.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import jzam.arcedex.models.PokeResearch

/*
 * Intermediary class for accessing PokeResearch database info.
 */
class PokeResearchRepository(private val pokeResearchDao: PokeResearchDao) {

    fun getResearchTasks(): LiveData<List<PokeResearch>> {
        return pokeResearchDao.getResearchTasks()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(task: PokeResearch) {
        pokeResearchDao.insert(task)
    }

    suspend fun delete(task: PokeResearch) {
        pokeResearchDao.delete(task)
    }
}