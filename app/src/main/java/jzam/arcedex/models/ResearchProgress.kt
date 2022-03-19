package jzam.arcedex.models

/*
 * Keeps track of research progress for a specific Pokemon
 */
data class ResearchProgress(
    val name: String,
    var goalsDone: Int = 0,
    var goalsTotal: Int,
    var pointsDone: Int = 0,
    var pointsTotal: Int,
    var bonusEarned: Int = 0
)
