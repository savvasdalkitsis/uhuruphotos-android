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
package com.savvasdalkitsis.uhuruphotos.implementation.gallery.trash.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffect
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.GalleryPageState
import com.savvasdalkitsis.uhuruphotos.api.seam.CompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.api.seam.Either
import com.savvasdalkitsis.uhuruphotos.api.seam.Mutation
import com.savvasdalkitsis.uhuruphotos.api.seam.Seam
import com.savvasdalkitsis.uhuruphotos.api.seam.SeamViaHandler.Companion.handler
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.trash.seam.TrashAction
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.trash.seam.TrashActionHandler
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.trash.seam.TrashAlbumPageActionHandler
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.trash.seam.TrashEffect
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.trash.state.TrashState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class TrashViewModel @Inject constructor(
    trashActionHandler: TrashActionHandler,
    trashAlbumPageActionHandler: TrashAlbumPageActionHandler,
): ViewModel(), Seam<
        Pair<GalleryPageState, TrashState>,
        Either<GalleryPageEffect, TrashEffect>,
        Either<GalleryPageAction, TrashAction>,
        Mutation<Pair<GalleryPageState, TrashState>>> by handler(
    CompositeActionHandler(
        trashAlbumPageActionHandler,
        trashActionHandler,
    ),
    GalleryPageState() to TrashState(),
)
