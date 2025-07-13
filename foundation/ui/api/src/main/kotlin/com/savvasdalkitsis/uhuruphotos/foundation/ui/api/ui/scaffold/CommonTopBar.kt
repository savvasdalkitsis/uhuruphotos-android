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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.insets.insetsEnd
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.insets.insetsStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopBar(
    topBarDisplayed: Boolean = true,
    toolbarColor: @Composable () -> Color = { MaterialTheme.colorScheme.background.copy(alpha = 0.8f) },
    title: @Composable () -> Unit,
    expandable: Boolean = false,
    navigationIcon: @Composable (() -> Unit)? = null,
    actionBarContent: @Composable (RowScope.() -> Unit) = {},
    alternativeTopBar: @Composable () -> Unit = {},
    showAlternativeTopBar: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
) {
    AnimatedVisibility(
        visible = topBarDisplayed,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Row(
            modifier = Modifier
                .recomposeHighlighter()
                .background(toolbarColor())
        ) {
            Spacer(modifier = Modifier.width(insetsStart()))
            Column(
                modifier = Modifier
                    .recomposeHighlighter()
                    .weight(1f)
            ) {
                AnimatedContent(
                    targetState = showAlternativeTopBar,
                    label = "topBar",
                ) { show ->
                    when {
                        show -> alternativeTopBar()
                        else -> ExpandableTopAppBar(
                            title = title,
                            backgroundColor = Color.Transparent,
                            navigationIcon = navigationIcon,
                            expandable = expandable,
                            actions = {
                                actionBarContent()
                            },
                            scrollBehavior = scrollBehavior,
                        )
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .width(insetsEnd())
                    .background(toolbarColor())
            )
        }
    }
}