/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.ui.view

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.insets.ui.Scaffold
import com.savvasdalkitsis.uhuruphotos.ui.insets.insetsEnd
import com.savvasdalkitsis.uhuruphotos.ui.insets.insetsStart

@Composable
fun CommonScaffold(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = { Logo() },
    bottomBarContent: @Composable () -> Unit = {},
    actionBarContent: @Composable (RowScope.() -> Unit) = {},
    toolbarColor: @Composable () -> Color = { MaterialTheme.colors.background.copy(alpha = 0.8f) },
    bottomBarColor: @Composable () -> Color = { MaterialTheme.colors.background.copy(alpha = 0.8f) },
    topBarDisplayed: Boolean = true,
    bottomBarDisplayed: Boolean = true,
    navigationIcon: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) = CommonScaffold(
    modifier = modifier,
    topBar = {
        CommonTopBar(
            topBarDisplayed,
            toolbarColor,
            title,
            navigationIcon,
            actionBarContent
        )
    },
    bottomBarContent = bottomBarContent,
    bottomBarColor = bottomBarColor,
    bottomBarDisplayed = bottomBarDisplayed,
    content = content,
)

@Composable
fun CommonScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    bottomBarContent: @Composable () -> Unit = {},
    bottomBarColor: @Composable () -> Color = { MaterialTheme.colors.background.copy(alpha = 0.8f) },
    bottomBarDisplayed: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier
            .imePadding(),
        bottomBar = {
            AnimatedVisibility(
                visible = bottomBarDisplayed,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it } ),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            ) {
                Box(
                    Modifier
                        .background(bottomBarColor())
                        .navigationBarsPadding()
                ) {
                    bottomBarContent()
                }
            }
        },
        topBar = topBar
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