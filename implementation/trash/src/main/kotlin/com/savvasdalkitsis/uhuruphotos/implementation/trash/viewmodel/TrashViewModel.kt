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
package com.savvasdalkitsis.uhuruphotos.implementation.trash.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageEffect
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumPageState
import com.savvasdalkitsis.uhuruphotos.api.seam.CompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.api.seam.Either
import com.savvasdalkitsis.uhuruphotos.api.seam.Mutation
import com.savvasdalkitsis.uhuruphotos.api.seam.Seam
import com.savvasdalkitsis.uhuruphotos.api.seam.SeamViaHandler.Companion.handler
import com.savvasdalkitsis.uhuruphotos.implementation.trash.seam.TrashAction
import com.savvasdalkitsis.uhuruphotos.implementation.trash.seam.TrashActionHandler
import com.savvasdalkitsis.uhuruphotos.implementation.trash.seam.TrashAlbumPageActionHandler
import com.savvasdalkitsis.uhuruphotos.implementation.trash.seam.TrashEffect
import com.savvasdalkitsis.uhuruphotos.implementation.trash.view.state.TrashState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class TrashViewModel @Inject constructor(
    trashActionHandler: TrashActionHandler,
    trashAlbumPageActionHandler: TrashAlbumPageActionHandler,
): ViewModel(), Seam<
        Pair<AlbumPageState, TrashState>,
        Either<AlbumPageEffect, TrashEffect>,
        Either<AlbumPageAction, TrashAction>,
        Mutation<Pair<AlbumPageState, TrashState>>> by handler(
    CompositeActionHandler(
        trashAlbumPageActionHandler,
        trashActionHandler,
    ),
    AlbumPageState() to TrashState(),
)
