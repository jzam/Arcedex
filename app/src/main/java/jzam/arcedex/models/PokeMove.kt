package jzam.arcedex.models

import androidx.compose.ui.graphics.Color

/*
 * Pokemon move
 */
data class PokeMove(
    val name: String,
    val type: String,
    val power: String,
)

data class MoveColor(
    val type: String,
    val color: Color
)
