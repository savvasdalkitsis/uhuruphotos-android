package com.savvasdalkitsis.uhuruphotos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.exoplayer2.ExoPlayer
import com.savvasdalkitsis.uhuruphotos.navigation.Navigator
import com.savvasdalkitsis.uhuruphotos.ui.window.LocalSystemUiController
import com.savvasdalkitsis.uhuruphotos.ui.window.WindowSize
import com.savvasdalkitsis.uhuruphotos.ui.window.windowSizeClass
import com.savvasdalkitsis.uhuruphotos.video.LocalExoPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : ComponentActivity() {

    @Inject lateinit var navigator: Navigator
    @Inject lateinit var exoPlayer: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val systemUiController = rememberSystemUiController()
            val (width, height) = windowSizeClass()
            CompositionLocalProvider(
                LocalSystemUiController provides systemUiController,
                LocalExoPlayer provides exoPlayer,
                WindowSize.LOCAL_WIDTH provides width,
                WindowSize.LOCAL_HEIGHT provides height
            ) {
                navigator.NavigationTargets()
            }
        }
    }
}