/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.api.module.AutoAlbumModule
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.seam.AutoAlbumActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.AuthModule
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.module.GalleryModule
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.module.UserModule
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule

internal object AutoAlbumModule {

    val autoAlbumActionsContext get() = AutoAlbumActionsContext(
        AutoAlbumModule.autoAlbumUseCase,
        AuthModule.serverUseCase,
        DateModule.dateDisplayer,
        GalleryModule.galleryActionsContextFactory,
        UserModule.userUseCase,
    )
}