package com.savvasdalkitsis.uhuruphotos.ui.view

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ui.Scaffold
import com.savvasdalkitsis.uhuruphotos.ui.insets.insetsBottom
import com.savvasdalkitsis.uhuruphotos.ui.insets.insetsEnd
import com.savvasdalkitsis.uhuruphotos.ui.insets.insetsStart
import com.savvasdalkitsis.uhuruphotos.ui.insets.insetsTop

@Composable
fun CommonScaffold(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = { Logo() },
    bottomBarContent: @Composable () -> Unit = {},
    actionBarContent: @Composable() (RowScope.() -> Unit) = {},
    toolbarColor: @Composable () -> Color = { MaterialTheme.colors.background.copy(alpha = 0.8f) },
    bottomBarColor: @Composable () -> Color = { MaterialTheme.colors.background.copy(alpha = 0.8f) },
    topBarDisplayed: Boolean = true,
    bottomBarDisplayed: Boolean = true,
    navigationIcon: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            AnimatedVisibility(
                visible = bottomBarDisplayed,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it } ),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            ) {
                Column(
                    Modifier
                        .background(bottomBarColor())
                ) {
                    bottomBarContent()
                    Spacer(
                        modifier = Modifier
                            .height(insetsBottom())
                            .fillMaxWidth()
                    )
                }
            }
        },
        topBar = {
            AnimatedVisibility(
                visible = topBarDisplayed,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Row(modifier = Modifier
                    .background(toolbarColor())
                ) {
                    Spacer(modifier = Modifier.width(insetsStart()))
                    Column(modifier = Modifier
                        .weight(1f)
                    ) {
                        Spacer(modifier = Modifier.height(insetsTop()))
                        TopAppBar(
                            title = title,
                            backgroundColor = Color.Transparent,
                            elevation = 0.dp,
                            navigationIcon = navigationIcon,
                            actions = {
                                actionBarContent()
                            }
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .width(insetsEnd())
                            .background(toolbarColor())
                    )
                }
            }
        }
    ) { contentPadding ->
        Surface(
            modifier = Modifier
                .padding(
                    start = insetsStart(),
                    end = insetsEnd(),
                )
                .fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            content(contentPadding)
        }
    }
}