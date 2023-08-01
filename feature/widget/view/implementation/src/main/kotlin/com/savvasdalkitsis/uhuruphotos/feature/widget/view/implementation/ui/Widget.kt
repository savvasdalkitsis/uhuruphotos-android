/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.WidgetDependencies
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailWithNetworkCacheImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Action
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Effect
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Seam
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

abstract class Widget<S : Any, E : Effect<EC>, A : Action<S, E, AC>, AC: Any, EC: Any>(
    private val widgetDependencies: WidgetDependencies,
    private val actionContext: AC,
    private val effectsContext: EC,
    initialState: S,
    private val initialAction: A,
) : GlanceAppWidget() {

    private val seam = Seam<S, E, A>(
        { state, action, effect ->
            with(actionContext) {
                action.handle(state, effect)
            }
        },
        { effect ->
            with(effectsContext) {
                effect.handle()
            }
        },
        initialState,
        MainScope()
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                CompositionLocalProvider(
                    LocalThumbnailWithNetworkCacheImageLoader provides widgetDependencies.imageLoader
                ) {
                    val scope = rememberCoroutineScope()

                    val action: (A) -> Unit = {
                        scope.launch {
                            seam.action(it)
                        }
                    }
                    val state by seam.state.collectAsState()
                    WidgetContainer {
                        Content(state, action)
                    }
                    LaunchedEffect(Unit) {
                        action(initialAction)
                    }
                }
            }
        }
    }

    @Composable
    abstract fun Content(state: S, action: (A) -> Unit)

    @Composable
    private fun WidgetContainer(content: @Composable () -> Unit) {
        Box(
            modifier = GlanceModifier
                .background(GlanceTheme.colors.background)
                .fillMaxSize()
                .cornerRadius(20.dp),
            content = content
        )
    }
}