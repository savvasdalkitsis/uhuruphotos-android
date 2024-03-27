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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.controller

import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.CollapsibleGroupState
import javax.inject.Inject

class SettingsViewStateController @Inject constructor(
    @PlainTextPreferences
    private val preferences: Preferences,
) {
    private val allGroups = mutableSetOf<CollapsibleGroupState>()

    val ui = group(string.ui, "settings:group:ui")
    val uiFeed = group(string.feed, "settings:group:ui:feed")
    val uiCollage = group(string.collage, "settings:group:ui:collage")
    val uiProgress = group(string.progress, "settings:group:ui:progress")
    val uiTheme = group(string.theme, "settings:group:ui:theme")
    val uiSearch = group(string.search, "settings:group:ui:search")
    val uiLibrary = group(string.library, "settings:group:ui:library")
    val uiMaps = group(string.maps, "settings:group:ui:maps")
    val uiVideo = group(string.video, "settings:group:ui:video")
    val privacy = group(string.privacy_security, "settings:group:privacy")
    val privacyBiometrics = group(string.biometrics, "settings:group:privacy:biometrics")
    val privacyShare = group(string.share, "settings:group:privacy:share")
    val jobs = group(string.jobs, "settings:group:jobs")
    val jobsFeedConfiguration = group(string.feed_sync_job_configuration, "settings:group:jobs:feedConfiguration")
    val jobsStatus = group(string.status, "settings:group:jobs:status")
    val jobsCloudSync = group(string.cloud_sync, "settings:group:jobs:cloudSync")
    val advanced = group(string.advanced, "settings:group:advanced")
    val advancedLightboxPhotoDiskCache = group(string.lightbox_photo_disk_cache, "settings:group:advanced:lightbox:photo:diskCache")
    val advancedLightboxPhotoMemoryCache = group(string.lightbox_photo_memory_cache, "settings:group:advanced:lightbox:photo:memoryCache")
    val advancedThumbnailDiskCache = group(string.thumbnail_disk_cache, "settings:group:advanced:thumbnail:diskCache")
    val advancedThumbnailMemoryCache = group(string.thumbnail_memory_cache, "settings:group:advanced:thumbnail:memoryCache")
    val advancedVideoDiskCache = group(string.video_disk_cache, "settings:group:advanced:video:diskCache")
    val advancedLocalMedia = group(string.local_media, "settings:group:advanced:localMedia")
    val help = group(string.help, "settings:group:help")
    val helpFeedback = group(string.feedback, "settings:group:help:feedback")


    fun collapseAll() {
        allGroups.forEach { it.collapse() }
    }

    fun expandAll() {
        allGroups.forEach { it.expand() }
    }

    private fun group(
        @StringRes title: Int,
        key: String,
    ): CollapsibleGroupState = CollapsibleGroupState(title, preferencesState(key))
        .also { allGroups += it }

    private fun preferencesState(key: String) = object : MutableState<Boolean> {
        private val state = mutableStateOf(preferences.get(key, true))

        override var value: Boolean
            get() = state.value
            set(value) {
                preferences.set(key, value)
                state.value = value
            }
        override fun component1(): Boolean = value
        override fun component2(): (Boolean) -> Unit = { value = it }
    }

}