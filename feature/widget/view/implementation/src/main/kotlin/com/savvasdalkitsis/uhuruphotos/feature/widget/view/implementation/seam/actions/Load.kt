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
package com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.seam.actions

import com.github.michaelbull.result.Ok
import com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.seam.FavouritesWidgetMutation.ShowFavourites
import com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.seam.WidgetActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.seam.effects.WidgetEffect
import com.savvasdalkitsis.uhuruphotos.feature.widget.view.implementation.ui.state.FavouritesWidgetState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

data object Load : FavouritesWidgetAction() {
    context(WidgetActionsContext) override fun handle(
        state: FavouritesWidgetState,
        effect: EffectHandler<WidgetEffect>
    ): Flow<Mutation<FavouritesWidgetState>> = mediaUseCase.observeFavouriteMedia()
        .mapNotNull { it as? Ok }
        .map { media -> ShowFavourites(media.value.map { it.id }) }
}