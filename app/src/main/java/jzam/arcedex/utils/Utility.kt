package jzam.arcedex.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.LocaleList
import jzam.arcedex.data.PokeMovesData
import jzam.arcedex.data.PokeResearchData
import jzam.arcedex.data.PokeTranslateData
import jzam.arcedex.models.SupportedLanguage

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
    lang: SupportedLanguage,
    goalsDone: Int, goalsTotal: Int, pointsDone: Int,
    pointsTotal: Int
): String {
    return (translate(lang, "Tasks") + ": $goalsDone/$goalsTotal | " +
            translate(lang, "Points") + ": $pointsDone/$pointsTotal")
}

fun formatSearchedText(lang: SupportedLanguage, searchText: String): String {
    return translate(lang, "Searched for") + " $searchText"
}

fun getResearchRank(lang: SupportedLanguage, points: Int): String {
    val ranks = PokeResearchData.ranks
    val rank = when {
        points < ranks[1] -> "0"
        points < ranks[2] -> "1"
        points < ranks[3] -> "2"
        points < ranks[4] -> "3"
        points < ranks[5] -> "4"
        points < ranks[6] -> "5"
        points < ranks[7] -> "6"
        points < ranks[8] -> "7"
        points < ranks[9] -> "8"
        points < ranks[10] -> "9"
        else -> "10"
    }
    return translate(lang, "Research Rank") + " " + rank
}

fun getPointsToNextRankText(lang: SupportedLanguage, points: Int): String {
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
    return translate(lang, "Points to next rank:") + " " + pointsNeeded
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

fun translate(lang: SupportedLanguage, text: String): String {

    val translation = when (lang) {
        SupportedLanguage.JAPANESE -> PokeTranslateData.jp.find { it.oldText == text }
        else -> null
    }
    if (translation != null) {
        return translation.newText
    } else {
        return text
    }
}

fun getSupportedLanguage(locales: LocaleList): SupportedLanguage {
    for (locale in locales) {
        if (PokeTranslateData.languages.contains(locale.language)) {
            return when (locale.language) {
                "ja" -> SupportedLanguage.JAPANESE
                else -> SupportedLanguage.ENGLISH
            }
        }
    }
    return SupportedLanguage.ENGLISH
}

fun getMoveType(task: String): String {
    val moveName = task.replace("Times you’ve seen it use ", "")
    val move = PokeMovesData.moves.find { it.name == moveName }
    if (move != null && move.power != "—") {
        return (move.type)
    }
    return ""
}

fun getTypeColor(type: String): Color {
    val typeColor = PokeMovesData.typeColors.find { it.type == type}
    if (typeColor != null) {
        return typeColor.color
    }
    return Color.White
}