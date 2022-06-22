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
package com.savvasdalkitsis.uhuruphotos.api.albumpage.seam

import com.savvasdalkitsis.uhuruphotos.api.coroutines.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction.ChangeFeedDisplay
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction.LoadAlbum
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction.PersonSelected
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction.SelectedPhoto
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction.SwipeToRefresh
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageEffect.OpenPhotoDetails
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageMutation.ErrorLoading
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageMutation.Loading
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageMutation.ShowAlbumPage
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumDetails
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumPageState
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import java.io.IOException
import kotlin.properties.Delegates

class AlbumPageActionHandler(
    private val albumRefresher: suspend (Int) -> Unit,
    private val albumDetailsFlow: (albumId: Int) -> Flow<AlbumDetails>,
    private val photoSequenceDataSource: (albumId: Int) -> PhotoSequenceDataSource,
    private val initialFeedDisplay: (albumId: Int) -> FeedDisplay,
    private val feedDisplayPersistence: suspend (albumId:Int, FeedDisplays) -> Unit,
) : ActionHandler<AlbumPageState, AlbumPageEffect, AlbumPageAction, AlbumPageMutation> {

    private val loading = MutableSharedFlow<AlbumPageMutation>()
    private var albumId by Delegates.notNull<Int>()

    override fun handleAction(
        state: AlbumPageState,
        action: AlbumPageAction,
        effect: suspend (AlbumPageEffect) -> Unit,
    ): Flow<AlbumPageMutation> = when (action) {
        is LoadAlbum -> {
            merge(
                flowOf(AlbumPageMutation.ChangeFeedDisplay(initialFeedDisplay(action.albumId))),
                albumDetailsFlow(action.albumId)
                    .map(::ShowAlbumPage),
                loading
            ).safelyOnStartIgnoring {
                albumId = action.albumId
                refreshAlbum()
            }
        }
        SwipeToRefresh -> flow {
            refreshAlbum()
        }
        is SelectedPhoto -> flow {
            effect(
                with(action) {
                    OpenPhotoDetails(
                        id = photo.id,
                        center = center,
                        scale = scale,
                        video = photo.isVideo,
                        photoSequenceDataSource = photoSequenceDataSource(albumId)
                    )
                }
            )
        }
        NavigateBack -> flow {
            effect(AlbumPageEffect.NavigateBack)
        }
        is PersonSelected -> flow {
            effect(NavigateToPerson(action.person.id))
        }
        is ChangeFeedDisplay -> flow {
            emit(AlbumPageMutation.ChangeFeedDisplay(action.feedDisplay))
            (action.feedDisplay as? FeedDisplays)?.let {
                feedDisplayPersistence(albumId, it)
            }
        }
    }

    private suspend fun refreshAlbum() {
        loading.emit(Loading(true))
        try {
            albumRefresher(albumId)
        } catch (e: IOException) {
            log(e)
            loading.emit(ErrorLoading)
        } finally {
            loading.emit(Loading(false))
        }
    }
}
