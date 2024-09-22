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
package com.savvasdalkitsis.uhuruphotos.feature.local.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.local.domain.api.usecase.LocalAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaFolderOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
internal class LocalAlbumUseCase @Inject constructor(
    private val mediaUseCase: MediaUseCase,
    private val localMediaUseCase: LocalMediaUseCase,
    @PlainTextPreferences
    private val preferences: Preferences,
) : LocalAlbumUseCase {

    override fun observeLocalAlbum(albumId: Int): Flow<Pair<LocalMediaFolder, List<MediaCollection>>> =
        mediaUseCase.observeLocalAlbum(albumId)
            .distinctUntilChanged()
            .mapNotNull { localMedia ->
                when (localMedia) {
                    is MediaFolderOnDevice.Found ->
                        localMedia.folder.first to localMedia.folder.second.toMediaCollections()
                    else -> null
                }
            }

    override suspend fun refreshLocalAlbum(albumId: Int) =
        localMediaUseCase.refreshLocalMediaFolder(albumId)

    override fun getLocalAlbumGalleryDisplay(albumId: Int) : PredefinedCollageDisplayState =
        preferences.get(key(albumId), PredefinedCollageDisplayState.default)

    override fun setLocalAlbumGalleryDisplay(albumId: Int, galleryDisplay: PredefinedCollageDisplayState) {
        preferences.set(key(albumId), galleryDisplay)
    }

    override suspend fun getLocalAlbum(albumId: Int): List<MediaCollection> =
        observeLocalAlbum(albumId).first().second

    private fun key(albumId: Int) = "localFolderGalleryDisplay/$albumId"

    private fun List<MediaItem>.toMediaCollections(): List<MediaCollection> =
        groupBy { it.displayDayDate }
            .map { (date, items) ->
                MediaCollection(
                    id = "local_album_$date",
                    unformattedDate = items.first().sortableDate,
                    displayTitle = date ?: "-",
                    mediaItems = items,
                    location = null,
                )
            }
}
