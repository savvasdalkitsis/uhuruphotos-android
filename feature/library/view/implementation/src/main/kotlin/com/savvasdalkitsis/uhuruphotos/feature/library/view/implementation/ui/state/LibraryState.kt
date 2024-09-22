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
package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state

import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class LibraryState(
    val loading: Boolean = false,
    val showTrash: Boolean = true,
    val showHidden: Boolean = true,
    val autoAlbums: VitrineState? = null,
    val userAlbums: VitrineState? = null,
    val favouritePhotos: VitrineState? = null,
    val localMedia: LibraryLocalMediaState? = null,
    val items: ImmutableList<LibraryItemState> = persistentListOf(),
    val showUpsellDialogFromSource: LibraryItemState? = null,
)