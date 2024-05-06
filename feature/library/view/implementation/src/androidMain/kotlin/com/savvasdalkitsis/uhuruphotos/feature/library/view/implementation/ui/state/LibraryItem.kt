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

import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import dev.icerock.moko.resources.StringResource

enum class LibraryItem(
    val title: StringResource,
) {
    TRASH(strings.trash),
    HIDDEN(strings.hidden_media),
    LOCAL(strings.local_albums),
    AUTO(strings.auto_albums),
    USER(strings.user_albums),
    FAVOURITE(strings.favourite_photos);
}