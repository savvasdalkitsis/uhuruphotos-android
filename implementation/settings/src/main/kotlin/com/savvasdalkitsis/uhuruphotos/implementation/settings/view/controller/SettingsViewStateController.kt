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
package com.savvasdalkitsis.uhuruphotos.implementation.settings.view.controller

import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import javax.inject.Inject

class SettingsViewStateController @Inject constructor(
    private val flowSharedPreferences: FlowSharedPreferences,
) {
    private val allGroups = mutableSetOf<SettingsGroupState>()

    val ui = group(R.string.ui, "settings:group:ui")
    val uiTheme = group(R.string.theme, "settings:group:ui:theme")
    val uiSearch = group(R.string.search, "settings:group:ui:search")
    val uiLibrary = group(R.string.library, "settings:group:ui:library")
    val uiMaps = group(R.string.maps, "settings:group:ui:maps")
    val privacy = group(R.string.privacy_security, "settings:group:privacy")
    val privacyBiometrics = group(R.string.biometrics, "settings:group:privacy:biometrics")
    val privacyShare = group(R.string.share, "settings:group:privacy:share")
    val jobs = group(R.string.jobs, "settings:group:jobs")
    val jobsFeed = group(R.string.feed, "settings:group:jobs:feed")
    val advanced = group(R.string.advanced, "settings:group:advanced")
    val advancedImageDiskCache = group(R.string.image_disk_cache, "settings:group:advanced:image:diskCache")
    val advancedImageMemoryCache = group(R.string.image_memory_cache, "settings:group:advanced:image:memoryCache")
    val advancedVideoDiskCache = group(R.string.video_disk_cache, "settings:group:advanced:video:diskCache")
    val help = group(R.string.help, "settings:group:help")
    val helpFeedback = group(R.string.feedback, "settings:group:help:feedback")


    fun collapseAll() {
        allGroups.forEach { it.collapse() }
    }

    fun expandAll() {
        allGroups.forEach { it.expand() }
    }

    private fun group(
        @StringRes title: Int,
        key: String,
    ): SettingsGroupState = SettingsGroupState(title, preferencesState(key))
        .also { allGroups += it }

    private fun preferencesState(key: String) = object : MutableState<Boolean> {
        private val pref = flowSharedPreferences.getBoolean(key, true)
        private val state = mutableStateOf(pref.get())

        override var value: Boolean
            get() = state.value
            set(value) {
                pref.set(value)
                state.value = value
            }
        override fun component1(): Boolean = value
        override fun component2(): (Boolean) -> Unit = { value = it }
    }

}