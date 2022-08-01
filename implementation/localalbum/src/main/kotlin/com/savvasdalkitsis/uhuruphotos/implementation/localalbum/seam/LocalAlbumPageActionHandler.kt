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
package com.savvasdalkitsis.uhuruphotos.implementation.localalbum.seam

import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageActionHandler
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageEffect
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageMutation
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumDetails
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumPageState
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.Title
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.LocalBucket
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.implementation.localalbum.usecase.LocalAlbumUseCase
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class LocalAlbumPageActionHandler @Inject constructor(
    localAlbumUseCase: LocalAlbumUseCase,
) : ActionHandler<AlbumPageState, AlbumPageEffect, AlbumPageAction, AlbumPageMutation>
by AlbumPageActionHandler(
    albumRefresher = { localAlbumUseCase.refreshLocalAlbum(it) },
    initialFeedDisplay = { localAlbumUseCase.getLocalAlbumFeedDisplay(it) },
    feedDisplayPersistence = { id, feedDisplay ->
        localAlbumUseCase.setLocalAlbumFeedDisplay(id, feedDisplay)
    },
    albumDetailsEmptyCheck = { albumId ->
        (localAlbumUseCase.getLocalAlbum(albumId) as? LocalBucket.Found)
            ?.bucket?.second?.isEmpty() != false
    },
    albumDetailsFlow = { albumId, _ ->
        localAlbumUseCase.observeLocalAlbum(albumId)
            .map { (bucket, albums) ->
                AlbumDetails(
                    title = Title.Text(bucket.displayName),
                    albums = albums,
                )
            }
    },
    photoSequenceDataSource = { PhotoSequenceDataSource.Single }
)
