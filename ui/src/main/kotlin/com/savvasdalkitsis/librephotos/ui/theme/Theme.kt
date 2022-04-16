package com.savvasdalkitsis.librephotos.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.savvasdalkitsis.librephotos.ui.window.LocalSystemUiController
import com.savvasdalkitsis.librephotos.ui.window.WindowSize
import com.savvasdalkitsis.librephotos.ui.window.windowSizeClass

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Color.Black,
    surface = Color.Black,
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200,
    onSecondary = Color.Gray,
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
fun Activity.AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val (width, height) = windowSizeClass()
    val systemUiController = rememberSystemUiController()
    CompositionLocalProvider(
        WindowSize.LOCAL_WIDTH provides width,
        WindowSize.LOCAL_HEIGHT provides height,
        LocalSystemUiController provides systemUiController
    ) {
        MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
        ) {
            val useDarkIcons = MaterialTheme.colors.isLight
            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = useDarkIcons
                )
            }
            content()
        }
    }
}