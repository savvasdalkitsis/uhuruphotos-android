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
package com.savvasdalkitsis.uhuruphotos.implementation.album.hidden.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffect
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.GalleryPageState
import com.savvasdalkitsis.uhuruphotos.api.seam.Either
import com.savvasdalkitsis.uhuruphotos.api.seam.Mutation
import com.savvasdalkitsis.uhuruphotos.api.seam.Seam
import com.savvasdalkitsis.uhuruphotos.api.seam.SeamViaHandler.Companion.handler
import com.savvasdalkitsis.uhuruphotos.implementation.album.hidden.seam.HiddenPhotosAction
import com.savvasdalkitsis.uhuruphotos.implementation.album.hidden.seam.HiddenPhotosCompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.implementation.album.hidden.seam.HiddenPhotosEffect
import com.savvasdalkitsis.uhuruphotos.implementation.album.hidden.seam.HiddenPhotosState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class HiddenPhotosViewModel @Inject constructor(
    hiddenPhotosCompositeActionHandler: HiddenPhotosCompositeActionHandler,
) : ViewModel(), Seam<
        Pair<GalleryPageState, HiddenPhotosState>,
        Either<GalleryPageEffect, HiddenPhotosEffect>,
        Either<GalleryPageAction, HiddenPhotosAction>,
        Mutation<Pair<GalleryPageState, HiddenPhotosState>>
> by handler(
    hiddenPhotosCompositeActionHandler,
    GalleryPageState(feedState = FeedState()) to HiddenPhotosState(),
)