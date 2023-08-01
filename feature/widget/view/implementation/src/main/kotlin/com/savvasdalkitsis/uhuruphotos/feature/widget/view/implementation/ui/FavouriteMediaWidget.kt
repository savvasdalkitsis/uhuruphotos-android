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

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.S
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.layout.Alignment
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.WidgetDependencies
import com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.seam.WidgetActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.seam.WidgetEffectsContext
import com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.seam.actions.FavouritesWidgetAction
import com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.seam.actions.OpenFavourite
import com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.seam.effects.WidgetEffect
import com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.ui.state.FavouritesWidgetState
import javax.inject.Inject

class FavouriteMediaWidget @Inject constructor(
    widgetDependencies: WidgetDependencies,
    actionsContext: WidgetActionsContext,
    effectsContext: WidgetEffectsContext,
) : Widget<FavouritesWidgetState, WidgetEffect, FavouritesWidgetAction, WidgetActionsContext, WidgetEffectsContext>(
    widgetDependencies,
    actionContext = actionsContext,
    effectsContext = effectsContext,
    initialState = FavouritesWidgetState(),
    initialAction = Load,
) {

    @Composable
    override fun Content(state: FavouritesWidgetState, action: (FavouritesWidgetAction) -> Unit) {
        LazyVerticalGrid(
            modifier = GlanceModifier.fillMaxSize(),
            gridCells = columns(),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {
            state.media.forEach { media ->
                item(media.value.hashCode().toLong()) {
                    Image(
                        modifier = GlanceModifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                        url = media.thumbnailUri,
                        onClick = {
                            action(OpenFavourite(media))
                        }
                    )
                }
            }
        }
    }

    private fun columns() = when {
        SDK_INT >= S -> GridCells.Adaptive(80.dp)
        else -> GridCells.Fixed(2)
    }
}
