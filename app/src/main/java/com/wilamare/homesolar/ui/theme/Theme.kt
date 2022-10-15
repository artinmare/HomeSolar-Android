package com.wilamare.homesolar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = PrimaryBlue,
    primaryVariant = Purple700,
    secondary = PrimaryGreen,
    background = DarkSurface,
    surface = LightDarkSurface,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val LightColorPalette = lightColors(
    primary = PrimaryBlue,
    primaryVariant = Purple700,
    secondary = PrimaryGreen,
    background = LightSurface,
    surface = LightBlue,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
    /* Other default colors to override
    background = Color.White,

    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun HomeSolarTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
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