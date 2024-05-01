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
package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.module.FeedModule
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.domain.api.module.HeatmapModule
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.module.CommonMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.module.LocalMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.ui.SettingsUiModule
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.module.NavigationModule
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.module.ToasterModule

internal object HeatMapModule {

    val heatMapActionsContext: HeatMapActionsContext
        get() = HeatMapActionsContext(
            FeedModule.feedUseCase,
            FeedModule.feedWorkScheduler,
            CommonMediaModule.mediaUseCase,
            SettingsUiModule.settingsUiUseCase,
            LocalMediaModule.localMediaWorkScheduler,
            AndroidModule.locationManager,
            HeatmapModule.heatmapUseCase,
            ToasterModule.toasterUseCase,
            NavigationModule.navigator,
        )
}