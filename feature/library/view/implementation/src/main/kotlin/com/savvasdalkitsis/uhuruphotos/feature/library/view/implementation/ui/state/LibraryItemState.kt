/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state

import org.jetbrains.compose.resources.StringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.auto_albums
import uhuruphotos_android.foundation.strings.api.generated.resources.favourite_media
import uhuruphotos_android.foundation.strings.api.generated.resources.hidden_media
import uhuruphotos_android.foundation.strings.api.generated.resources.local_albums
import uhuruphotos_android.foundation.strings.api.generated.resources.trash
import uhuruphotos_android.foundation.strings.api.generated.resources.user_albums

enum class LibraryItemState(
    val title: StringResource,
) {
    TRASH(string.trash),
    HIDDEN(string.hidden_media),
    LOCAL(string.local_albums),
    AUTO(string.auto_albums),
    USER(string.user_albums),
    FAVOURITE(string.favourite_media);
}