package com.savvasdalkitsis.uhuruphotos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.savvasdalkitsis.uhuruphotos.ui.window.LocalSystemUiController

private val DarkColorPalette = darkColors(
    primary = Color.White,
    primaryVariant = Color.White,
    secondary = Color.White,
    background = Color.Black,
    surface = Color.Black,
)

private val LightColorPalette = lightColors(
    primary = Color.Black,
    primaryVariant = Color.Black,
    secondary = Color.Black,
    background = Color.White,
    surface = Color.White,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

object CustomColors {
    val syncError = Color(158, 6, 37)
    val syncSuccess = Color(21, 158, 6, 255)
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
    ) {
        val isLight = MaterialTheme.colors.isLight
        val systemUiController = LocalSystemUiController.current
        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = isLight
            )
        }
        content()
    }
}