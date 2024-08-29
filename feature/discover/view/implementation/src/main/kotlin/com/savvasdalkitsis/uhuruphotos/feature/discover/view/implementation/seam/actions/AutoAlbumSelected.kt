/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.api.navigation.AutoAlbumNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.api.state.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.DiscoverActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.DiscoverMutation
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.DiscoverState
import kotlinx.coroutines.flow.flow

data class AutoAlbumSelected(val album: AutoAlbum) : DiscoverAction() {
    context(DiscoverActionsContext) override fun handle(
        state: DiscoverState
    ) = flow<DiscoverMutation> {
        navigator.navigateTo(AutoAlbumNavigationRoute(album.id))
    }
}
