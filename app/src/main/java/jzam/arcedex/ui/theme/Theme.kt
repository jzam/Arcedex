package jzam.arcedex.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = DarkBurgundy,
    primaryVariant = DarkYellow,
    secondary = DarkNavyBlue,
    secondaryVariant = DarkYellow,
    background = DarkGray,
    surface = DarkBurgundy,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onSurface = Color.White
)

private val LightColorPalette = lightColors(
    primary = Burgundy,
    primaryVariant = Color.Yellow,
    secondary = NavyBlue,
    secondaryVariant = NavyBlue,
    background = Linen,
)

@Composable
fun ArcedexTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}