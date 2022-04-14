package com.savvasdalkitsis.librephotos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.savvasdalkitsis.librephotos.app.navigation.Navigator
import com.savvasdalkitsis.librephotos.ui.theme.AppTheme
import com.savvasdalkitsis.librephotos.ui.window.window.LocalSystemUiController
import com.savvasdalkitsis.librephotos.ui.window.WindowSize
import com.savvasdalkitsis.librephotos.ui.window.window.windowSizeClass
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : ComponentActivity() {

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var controllersProvider: com.savvasdalkitsis.librephotos.app.navigation.ControllersProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val (width, height) = windowSizeClass()
            val systemUiController = rememberSystemUiController()
            CompositionLocalProvider(
                WindowSize.LOCAL_WIDTH provides width,
                WindowSize.LOCAL_HEIGHT provides height,
                LocalSystemUiController provides systemUiController
            ) {
                AppTheme {
                    val useDarkIcons = MaterialTheme.colors.isLight
                    val navController = rememberAnimatedNavController()

                    SideEffect {
                        systemUiController.setSystemBarsColor(
                            color = Color.Transparent,
                            darkIcons = useDarkIcons
                        )
                    }

                    navigator.NavigationTargets(navController)
                }
            }
        }
    }
}