package com.savvasdalkitsis.librephotos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import com.google.android.exoplayer2.ExoPlayer
import com.savvasdalkitsis.librephotos.navigation.Navigator
import com.savvasdalkitsis.librephotos.ui.theme.AppTheme
import com.savvasdalkitsis.librephotos.video.LocalExoPlayer
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
            CompositionLocalProvider(
                LocalExoPlayer provides exoPlayer
            ) {
                AppTheme {
                    navigator.NavigationTargets()
                }
            }
        }
    }
}