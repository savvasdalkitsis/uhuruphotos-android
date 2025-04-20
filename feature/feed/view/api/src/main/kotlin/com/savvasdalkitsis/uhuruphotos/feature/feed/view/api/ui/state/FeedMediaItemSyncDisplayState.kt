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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_invisible
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_swipe_vertical
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_visible
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.show_always
import uhuruphotos_android.foundation.strings.api.generated.resources.show_never
import uhuruphotos_android.foundation.strings.api.generated.resources.show_on_scroll

enum class FeedMediaItemSyncDisplayState(
    val friendlyName: StringResource,
    val icon: DrawableResource,
) {
    SHOW_ON_SCROLL(string.show_on_scroll, drawable.ic_swipe_vertical),
    ALWAYS_ON(string.show_always, drawable.ic_visible),
    ALWAYS_OFF(string.show_never, drawable.ic_invisible);

    companion object {
        val default = SHOW_ON_SCROLL
    }
}