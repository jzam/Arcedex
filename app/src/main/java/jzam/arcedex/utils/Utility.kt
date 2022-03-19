package jzam.arcedex.utils

import jzam.arcedex.data.PokeResearchData

/*
 * General utility methods for the app
 */
fun formatPokemonId(id: Int): String {
    return when {
        id < 10 -> {
            "No. 00" + id.toString()
        }
        id < 100 -> {
            "No. 0" + id.toString()
        }
        else -> {
            "No. " + id.toString()
        }
    }
}

fun formatPokemonResearchInfo(
    goalsDone: Int, goalsTotal: Int, pointsDone: Int,
    pointsTotal: Int
): String {
    return ("Tasks: $goalsDone / $goalsTotal | Points: $pointsDone / $pointsTotal")
}

fun formatSearchedText(searchText: String): String {
    return "Searched for $searchText"
}

fun getResearchRank(points: Int): String {
    val ranks = PokeResearchData.ranks
    return when {
        points < ranks[1] -> "Research Rank 0"
        points < ranks[2] -> "Research Rank 1"
        points < ranks[3] -> "Research Rank 2"
        points < ranks[4] -> "Research Rank 3"
        points < ranks[5] -> "Research Rank 4"
        points < ranks[6] -> "Research Rank 5"
        points < ranks[7] -> "Research Rank 6"
        points < ranks[8] -> "Research Rank 7"
        points < ranks[9] -> "Research Rank 8"
        points < ranks[10] -> "Research Rank 9"
        else -> "Research Rank 10"
    }
}

fun getPointsToNextRankText(points: Int): String {
    val ranks = PokeResearchData.ranks
    val pointsNeeded = when {
        points < ranks[1] -> ranks[1] - points
        points < ranks[2] -> ranks[2] - points
        points < ranks[3] -> ranks[3] - points
        points < ranks[4] -> ranks[4] - points
        points < ranks[5] -> ranks[5] - points
        points < ranks[6] -> ranks[6] - points
        points < ranks[7] -> ranks[7] - points
        points < ranks[8] -> ranks[8] - points
        points < ranks[9] -> ranks[9] - points
        points < ranks[10] -> ranks[10] - points
        else -> 0
    }
    return "Points to next rank: $pointsNeeded"
}

fun getRankProgress(points: Float): Float {
    val ranks = PokeResearchData.ranks
    return when {
        points < ranks[1] -> ((points - ranks[0]) / (ranks[1] - ranks[0]))
        points < ranks[2] -> ((points - ranks[1]) / (ranks[2] - ranks[1]))
        points < ranks[3] -> ((points - ranks[2]) / (ranks[3] - ranks[2]))
        points < ranks[4] -> ((points - ranks[3]) / (ranks[4] - ranks[3]))
        points < ranks[5] -> ((points - ranks[4]) / (ranks[5] - ranks[4]))
        points < ranks[6] -> ((points - ranks[5]) / (ranks[6] - ranks[5]))
        points < ranks[7] -> ((points - ranks[6]) / (ranks[7] - ranks[6]))
        points < ranks[8] -> ((points - ranks[7]) / (ranks[8] - ranks[7]))
        points < ranks[9] -> ((points - ranks[8]) / (ranks[9] - ranks[8]))
        points < ranks[10] -> ((points - ranks[9]) / (ranks[10] - ranks[9]))
        else -> 1f
    }
}