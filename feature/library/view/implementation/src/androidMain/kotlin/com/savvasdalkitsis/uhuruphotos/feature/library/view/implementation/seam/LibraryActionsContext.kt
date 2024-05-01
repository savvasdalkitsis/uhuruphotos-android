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
package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.api.usecase.AutoAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.domain.api.usecase.UserAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItem
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.usecase.LibraryUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.observe
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class LibraryActionsContext(
    val autoAlbumsUseCase: AutoAlbumsUseCase,
    val userAlbumsUseCase: UserAlbumsUseCase,
    val mediaUseCase: MediaUseCase,
    val localMediaUseCase: LocalMediaUseCase,
    val libraryUseCase: LibraryUseCase,
    val navigator: Navigator,
    val welcomeUseCase: WelcomeUseCase,
    private val preferences: Preferences,
    private val toaster: ToasterUseCase,
    private val localMediaWorkScheduler: LocalMediaWorkScheduler,
) {

    private val _loading = MutableSharedFlow<Boolean>()
    val loading: Flow<Boolean> get() = _loading

    suspend fun refreshAutoAlbums() {
        refresh {
            autoAlbumsUseCase.refreshAutoAlbums()
        }
    }

    suspend fun refreshUserAlbums() {
        refresh {
            userAlbumsUseCase.refreshUserAlbums()
        }
    }

    suspend fun refreshFavouriteMedia() {
        refresh {
            mediaUseCase.refreshFavouriteMedia()
        }
    }

    suspend fun refreshHiddenMedia() {
        refresh {
            mediaUseCase.refreshHiddenMedia()
        }
    }

    fun refreshLocalMedia() {
        localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
    }

    fun observeShouldShowUpsellFromSource(source: LibraryItem) =
        preferences.observe("upsellSourceKey::${source.name}", true)

    fun disableUpsellFromSource(source: LibraryItem) {
        preferences.set("upsellSourceKey::${source.name}", false)
    }

    private suspend fun refresh(
        refresh: suspend () -> SimpleResult,
    ) {
        _loading.emit(true)
        val result = refresh()
        if (result.isErr) {
            toaster.show(R.string.error_loading_albums)
        }
        // delaying to give ui time to receive the new albums before
        // dismissing the loading bar since no albums logic relies on that
        delay(500)
        _loading.emit(false)
    }
}
