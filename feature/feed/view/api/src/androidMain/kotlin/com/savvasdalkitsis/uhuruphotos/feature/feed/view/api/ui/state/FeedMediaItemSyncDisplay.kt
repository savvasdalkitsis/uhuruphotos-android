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

import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource

enum class FeedMediaItemSyncDisplay(
    val friendlyName: StringResource,
    val icon: ImageResource,
) {
    SHOW_ON_SCROLL(strings.show_on_scroll, images.ic_swipe_vertical),
    ALWAYS_ON(strings.show_always, images.ic_visible),
    ALWAYS_OFF(strings.show_never, images.ic_invisible);

    companion object {
        val default = SHOW_ON_SCROLL
    }
}