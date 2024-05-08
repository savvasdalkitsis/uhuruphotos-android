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
package com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.PlatformAuthModule
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.DiscoverActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.module.FeedModule
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.domain.api.module.HeatmapModule
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.module.PeopleModule
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.api.module.SearchModule
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.ui.SettingsUiModule
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.module.WelcomeModule
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.module.NavigationModule
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.module.PreferencesModule
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.module.ToasterModule
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.module.UiModule

object DiscoverModule {

    val discoverActionsContext get() = DiscoverActionsContext(
        FeedModule.feedUseCase,
        SettingsUiModule.settingsUiUseCase,
        PeopleModule.peopleUseCase,
        PlatformAuthModule.serverUseCase,
        SearchModule.searchUseCase,
        HeatmapModule.heatmapUseCase,
        UiModule.uiUseCase,
        ToasterModule.toasterUseCase,
        NavigationModule.navigator,
        WelcomeModule.welcomeUseCase,
        PreferencesModule.plainTextPreferences,
    )
}