package jzam.arcedex.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * A PokemonResearch object represents a research task. Tasks are associated with a Pokemon (name).
 * Tasks have descriptions (task), up to 5 goals within the task, progress of goals completed,
 * and a points value for each goal completed.
 */
@Entity(tableName = "pokeresearch_table")
data class PokeResearch(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "task")
    val task: String,

    @ColumnInfo(name = "goal1")
    val goal1: String,

    @ColumnInfo(name = "goal2")
    val goal2: String,

    @ColumnInfo(name = "goal3")
    val goal3: String,

    @ColumnInfo(name = "goal4")
    val goal4: String,

    @ColumnInfo(name = "goal5")
    val goal5: String,

    @ColumnInfo(name = "points")
    val points: Int,

    @ColumnInfo(name = "goalProgress")
    var goalProgress: Int,

    @ColumnInfo(name = "totalGoals")
    var totalGoals: Int
)
