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
package com.savvasdalkitsis.uhuruphotos.implementation.feedpage.usecase

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.api.feedpage.usecase.FeedPageUseCase
import com.savvasdalkitsis.uhuruphotos.api.gallery.view.state.PredefinedGalleryDisplay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class FeedPageUseCase @Inject constructor(
    preferences: FlowSharedPreferences,
) : FeedPageUseCase {

    private val preference = preferences.getEnum("feedDisplay", defaultValue = PredefinedGalleryDisplay.default)

    override fun getFeedDisplay(): Flow<PredefinedGalleryDisplay> = preference.asFlow()

    override suspend fun setFeedDisplay(feedDisplay: PredefinedGalleryDisplay) {
        preference.setAndCommit(feedDisplay)
    }
}