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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.share.api.usecase.ShareUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.usecase.UiUseCase
import javax.inject.Inject

class FeedEffectsContext @Inject constructor(
    val navigator: Navigator,
    val shareUseCase: ShareUseCase,
    val toasterUseCase: ToasterUseCase,
    val uiUseCase: UiUseCase,
)