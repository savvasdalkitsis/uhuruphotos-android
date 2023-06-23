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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

enum class FeedMediaItemSyncDisplay(
    @StringRes val friendlyName: Int,
    @DrawableRes val icon: Int,
) {
    SHOW_ON_SCROLL(string.show_on_scroll, drawable.ic_swipe_vertical),
    ALWAYS_ON(string.show_always, drawable.ic_visible),
    ALWAYS_OFF(string.show_never, drawable.ic_invisible);

    companion object {
        val default = SHOW_ON_SCROLL
    }
}