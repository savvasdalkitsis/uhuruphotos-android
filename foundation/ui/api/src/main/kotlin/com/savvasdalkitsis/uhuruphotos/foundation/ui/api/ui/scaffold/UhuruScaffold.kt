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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.insets.insetsEnd
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.insets.insetsStart
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.Logo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UhuruScaffold(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = { Logo() },
    bottomBarContent: @Composable () -> Unit = {},
    actionBarContent: @Composable (RowScope.() -> Unit) = {},
    alternativeTopBar: @Composable () -> Unit = {},
    showAlternativeTopBar: Boolean = false,
    toolbarColor: @Composable () -> Color = { MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f) },
    bottomBarColor: @Composable () -> Color = { MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f) },
    topBarDisplayed: Boolean = true,
    bottomBarDisplayed: Boolean = true,
    expandableTopBar: Boolean = false,
    navigationIcon: @Composable (() -> Unit)? = null,
    content: @Composable BoxScope.(PaddingValues) -> Unit
) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    UhuruScaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .background(MaterialTheme.colorScheme.surfaceContainer),
        topBar = {
            CommonTopBar(
                topBarDisplayed = topBarDisplayed,
                toolbarColor = toolbarColor,
                title = title,
                expandable = expandableTopBar,
                navigationIcon = navigationIcon,
                actionBarContent = actionBarContent,
                alternativeTopBar = alternativeTopBar,
                showAlternativeTopBar = showAlternativeTopBar,
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBarContent = bottomBarContent,
        bottomBarColor = bottomBarColor,
        bottomBarDisplayed = bottomBarDisplayed,
        content = content,
    )
}

@Composable
fun UhuruScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    bottomBarContent: @Composable () -> Unit = {},
    bottomBarColor: @Composable () -> Color = { MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f) },
    bottomBarDisplayed: Boolean = true,
    content: @Composable BoxScope.(PaddingValues) -> Unit
) {
    var bottomBarHeight by remember {
        mutableIntStateOf(0)
    }
    Scaffold(
        modifier = modifier
            .recomposeHighlighter()
            .imePadding()
        ,
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
                        .onGloballyPositioned {
                            bottomBarHeight = it.size.height
                        }
                ) {
                    bottomBarContent()
                }
            }
        },
        topBar = topBar
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .recomposeHighlighter()
                .padding(
                    start = insetsStart(),
                    end = insetsEnd(),
                )
                .fillMaxSize(),
        ) {
            content(contentPadding)
        }
    }
}