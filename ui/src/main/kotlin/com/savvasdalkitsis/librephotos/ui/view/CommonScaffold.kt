package com.savvasdalkitsis.librephotos.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ui.Scaffold
import com.savvasdalkitsis.librephotos.ui.insets.systemPadding

@Composable
fun CommonScaffold(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = { Text("LibrePhotos") },
    bottomBar: @Composable () -> Unit = {},
    actionBarContent: @Composable (RowScope.() -> Unit) = {},
    toolbarColor: Color = MaterialTheme.colors.background.copy(alpha = 0.8f),
    topBarDisplayed: Boolean = true,
    navigationIcon: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        contentPadding = systemPadding(WindowInsetsSides.Bottom),
        bottomBar = { bottomBar() },
        topBar = {
            AnimatedVisibility(
                visible = topBarDisplayed,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Column {
                    Spacer(
                        modifier = Modifier
                            .height(
                                systemPadding(
                                    WindowInsetsSides.Top
                                ).calculateTopPadding()
                            )
                            .fillMaxWidth()
                            .background(toolbarColor)
                    )
                    TopAppBar(
                        title = title,
                        backgroundColor = toolbarColor,
                        elevation = 0.dp,
                        navigationIcon = navigationIcon,
                        actions = {
                            actionBarContent()
                        }
                    )
                }
            }
        }
    ) { contentPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            content(contentPadding)
        }
    }
}