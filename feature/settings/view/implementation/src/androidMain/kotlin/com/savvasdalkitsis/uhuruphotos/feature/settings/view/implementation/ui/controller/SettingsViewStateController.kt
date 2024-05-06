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

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.group.state.CollapsibleGroupState
import dev.icerock.moko.resources.StringResource

class SettingsViewStateController(
    private val preferences: Preferences,
) {
    private val allGroups = mutableSetOf<CollapsibleGroupState>()

    val ui = group(strings.ui, "settings:group:ui")
    val uiFeed = group(strings.feed, "settings:group:ui:feed")
    val uiCollage = group(strings.collage, "settings:group:ui:collage")
    val uiProgress = group(strings.progress, "settings:group:ui:progress")
    val uiTheme = group(strings.theme, "settings:group:ui:theme")
    val uiSearch = group(strings.search, "settings:group:ui:search")
    val uiLibrary = group(strings.library, "settings:group:ui:library")
    val uiMaps = group(strings.maps, "settings:group:ui:maps")
    val uiVideo = group(strings.video, "settings:group:ui:video")
    val privacy = group(strings.privacy_security, "settings:group:privacy")
    val privacyBiometrics = group(strings.biometrics, "settings:group:privacy:biometrics")
    val privacyShare = group(strings.share, "settings:group:privacy:share")
    val jobs = group(strings.jobs, "settings:group:jobs")
    val jobsFeedConfiguration = group(strings.feed_sync_job_configuration, "settings:group:jobs:feedConfiguration")
    val jobsStatus = group(strings.status, "settings:group:jobs:status")
    val jobsCloudSync = group(strings.cloud_sync, "settings:group:jobs:cloudSync")
    val advanced = group(strings.advanced, "settings:group:advanced")
    val advancedLightboxPhotoDiskCache = group(strings.lightbox_photo_disk_cache, "settings:group:advanced:lightbox:photo:diskCache")
    val advancedLightboxPhotoMemoryCache = group(strings.lightbox_photo_memory_cache, "settings:group:advanced:lightbox:photo:memoryCache")
    val advancedThumbnailDiskCache = group(strings.thumbnail_disk_cache, "settings:group:advanced:thumbnail:diskCache")
    val advancedThumbnailMemoryCache = group(strings.thumbnail_memory_cache, "settings:group:advanced:thumbnail:memoryCache")
    val advancedVideoDiskCache = group(strings.video_disk_cache, "settings:group:advanced:video:diskCache")
    val advancedLocalMedia = group(strings.local_media, "settings:group:advanced:localMedia")
    val help = group(strings.help, "settings:group:help")
    val helpFeedback = group(strings.feedback, "settings:group:help:feedback")


    fun collapseAll() {
        allGroups.forEach { it.collapse() }
    }

    fun expandAll() {
        allGroups.forEach { it.expand() }
    }

    private fun group(
        title: StringResource,
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