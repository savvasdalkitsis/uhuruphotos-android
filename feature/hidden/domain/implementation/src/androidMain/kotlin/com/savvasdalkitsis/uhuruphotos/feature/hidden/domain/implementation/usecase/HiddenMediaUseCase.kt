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
package com.savvasdalkitsis.uhuruphotos.feature.hidden.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.hidden.domain.api.usecase.HiddenMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
internal class HiddenMediaUseCase @Inject constructor(
    @PlainTextPreferences
    private val preferences: Preferences,
) : HiddenMediaUseCase {

    private val key = "hiddenMediaGalleryDisplay"

    override fun getHiddenMediaGalleryDisplay(): CollageDisplay =
        preferences.get(key, PredefinedCollageDisplay.default)

    override suspend fun setHiddenMediaGalleryDisplay(galleryDisplay: PredefinedCollageDisplay) {
        preferences.set(key, galleryDisplay)
    }
}