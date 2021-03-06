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
package com.savvasdalkitsis.uhuruphotos.implementation.photos.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.api.seam.Seam
import com.savvasdalkitsis.uhuruphotos.api.seam.SeamViaHandler.Companion.handler
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoActionHandler
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoEffect
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoMutation
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.PhotoState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    handler: PhotoActionHandler,
) : ViewModel(),
    Seam<PhotoState, PhotoEffect, PhotoAction, PhotoMutation> by handler(
        handler,
        PhotoState(),
    )