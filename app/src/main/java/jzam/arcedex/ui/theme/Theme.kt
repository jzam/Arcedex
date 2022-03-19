package jzam.arcedex.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//TODO Actually support dark mode with different color set
private val DarkColorPalette = darkColors(
    primary = Burgundy,
    primaryVariant = DarkBurgundy,
    secondary = NavyBlue,
    secondaryVariant = Color.Yellow,
    background = Linen,
)

private val LightColorPalette = lightColors(
    primary = Burgundy,
    primaryVariant = DarkBurgundy,
    secondary = NavyBlue,
    secondaryVariant = Color.Yellow,
    background = Linen,
)

@Composable
fun ArcedexTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        //TODO Dark mode not supported yet
        LightColorPalette
        //DarkColorPalette
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