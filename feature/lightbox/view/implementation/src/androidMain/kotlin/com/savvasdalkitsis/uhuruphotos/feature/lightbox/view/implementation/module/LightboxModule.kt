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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.api.module.AutoAlbumModule
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.api.module.UserAlbumModule
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.AuthModule
import com.savvasdalkitsis.uhuruphotos.feature.download.domain.api.module.DownloadModule
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.module.FeedModule
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.domain.api.module.LightboxModule
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.local.domain.api.module.LocalAlbumModule
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.module.CommonMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.module.LocalMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.module.RemoteMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.memories.domain.api.module.MemoriesModule
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.api.module.PersonModule
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.module.PortfolioModule
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.api.module.SearchModule
import com.savvasdalkitsis.uhuruphotos.feature.trash.domain.api.module.TrashModule
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.module.UploadModule
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.module.NavigationModule
import com.savvasdalkitsis.uhuruphotos.foundation.share.api.module.ShareModule
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.module.ToasterModule
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.module.UiModule

internal object LightboxModule {

    val lightboxActionsContext get() = LightboxActionsContext(
        CommonMediaModule.mediaUseCase,
        DownloadModule.downloadUseCase,
        UploadModule.uploadUseCase,
        PersonModule.personUseCase,
        FeedModule.feedUseCase,
        MemoriesModule.memoriesUseCase,
        SearchModule.searchUseCase,
        LocalAlbumModule.localAlbumUseCase,
        CommonMediaModule.metadataUseCase,
        UserAlbumModule.userAlbumUseCase,
        AutoAlbumModule.autoAlbumUseCase,
        TrashModule.trashUseCase,
        RemoteMediaModule.remoteMediaUseCase,
        PortfolioModule.portfolioUseCase,
        LocalMediaModule.localMediaUseCase,
        DateModule.displayingDateTimeFormat,
        AndroidModule.applicationContext,
        UiModule.uiUseCase,
        ShareModule.shareUseCase,
        ToasterModule.toasterUseCase,
        NavigationModule.navigator,
        AndroidModule.clipboardManager,
        LocalMediaModule.localMediaDeletionUseCase,
        AuthModule.serverUseCase,
        LightboxModule.lightboxUseCase,
    )
}