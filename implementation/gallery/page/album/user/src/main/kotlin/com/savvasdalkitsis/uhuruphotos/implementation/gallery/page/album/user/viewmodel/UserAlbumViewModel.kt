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
package com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.album.user.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomAction
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomEffect
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomMutation
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.ui.state.ShowroomState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Seam
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.handler
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.album.user.seam.UserAlbumActionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class UserAlbumViewModel @Inject constructor(
    userAlbumActionHandler: UserAlbumActionHandler,
) : ViewModel(),
    Seam<ShowroomState, ShowroomEffect, ShowroomAction, ShowroomMutation> by handler(
        userAlbumActionHandler,
        ShowroomState(galleryState = GalleryState())
    )