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
package com.savvasdalkitsis.uhuruphotos.implementation.autoalbums.seam

import com.savvasdalkitsis.uhuruphotos.api.albums.ui.state.AlbumSorting
import com.savvasdalkitsis.uhuruphotos.api.autoalbums.ui.state.AutoAlbum

sealed class AutoAlbumsAction {
    data class AutoAlbumSelected(val album: AutoAlbum) : AutoAlbumsAction()
    data class ChangeSorting(val sorting: AlbumSorting) : AutoAlbumsAction()

    object Load : AutoAlbumsAction()
    object NavigateBack : AutoAlbumsAction()
    object Refresh : AutoAlbumsAction()
}
