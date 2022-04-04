package com.savvasdalkitsis.librephotos.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.savvasdalkitsis.librephotos.navigation.LibrePhotosNavigator
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.ui.theme.AppTheme
import com.savvasdalkitsis.librephotos.window.WindowSize
import com.savvasdalkitsis.librephotos.window.windowSizeClass
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {

    @Inject lateinit var librePhotosNavigator: LibrePhotosNavigator
    @Inject lateinit var controllersProvider: ControllersProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val (width, height) = windowSizeClass()
            CompositionLocalProvider(
                WindowSize.LOCAL_WIDTH provides width,
                WindowSize.LOCAL_HEIGHT provides height,
            ) {
                AppTheme {
                    val systemUiController = rememberSystemUiController()
                    val useDarkIcons = MaterialTheme.colors.isLight
                    val navController = rememberNavController()

                    SideEffect {
                        systemUiController.setSystemBarsColor(
                            color = Color.Transparent,
                            darkIcons = useDarkIcons
                        )
                    }

                    librePhotosNavigator.NavigationTargets(navController)
                }
            }
        }
    }
}