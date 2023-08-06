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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam

import com.github.michaelbull.result.Err
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.domain.api.usecase.UserAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class UserAlbumsActionsContext @Inject constructor(
    val userAlbumsUseCase: UserAlbumsUseCase,
    val serverUseCase: ServerUseCase,
    val toaster: ToasterUseCase,
    val navigator: Navigator,
) {

    private val _loading = MutableSharedFlow<Boolean>()
    val loading: Flow<Boolean> get() = _loading

    suspend fun refreshAlbums() {
        _loading.emit(true)
        val result = userAlbumsUseCase.refreshUserAlbums()
        if (result is Err) {
            toaster.show(R.string.error_loading_user_albums)
        }
        // delaying to give ui time to receive the new albums before
        // dismissing the loading bar since no albums logic relies on that
        delay(500)
        _loading.emit(false)
    }
}
