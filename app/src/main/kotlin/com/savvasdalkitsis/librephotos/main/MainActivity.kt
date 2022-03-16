package com.savvasdalkitsis.librephotos.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Bottom
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Top
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.savvasdalkitsis.librephotos.main.view.MainView
import com.savvasdalkitsis.librephotos.main.view.preview.mainStatePreview
import com.savvasdalkitsis.librephotos.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = MaterialTheme.colors.isLight
            val toolbarColor = MaterialTheme.colors
                .background.copy(alpha = 0.8f)
            AppTheme {
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                }
                Scaffold(
                    contentPadding = WindowInsets
                        .systemBars
                        .only(Bottom)
                        .asPaddingValues(),
                    topBar = {
                        TopAppBar(
                            backgroundColor = toolbarColor,
                            contentPadding = WindowInsets.systemBars
                                .only(Top)
                                .asPaddingValues(),
                            title = { Text(text = "LibrePhotos") }
                        )
                    }
                ) { contentPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        MainView(contentPadding, mainStatePreview)
                    }
                }
            }
        }
    }
}