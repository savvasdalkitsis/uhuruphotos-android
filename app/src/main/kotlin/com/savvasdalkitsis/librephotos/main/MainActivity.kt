package com.savvasdalkitsis.librephotos.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.navigation.LibrePhotosNavigator
import com.savvasdalkitsis.librephotos.ui.theme.AppTheme
import com.savvasdalkitsis.librephotos.window.LocalSystemUiController
import com.savvasdalkitsis.librephotos.window.WindowSize
import com.savvasdalkitsis.librephotos.window.windowSizeClass
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalFoundationApi
@AndroidEntryPoint
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@FlowPreview
class MainActivity : ComponentActivity() {

    @Inject lateinit var librePhotosNavigator: LibrePhotosNavigator
    @Inject lateinit var controllersProvider: ControllersProvider

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

                    librePhotosNavigator.NavigationTargets(navController)
                }
            }
        }
    }
}