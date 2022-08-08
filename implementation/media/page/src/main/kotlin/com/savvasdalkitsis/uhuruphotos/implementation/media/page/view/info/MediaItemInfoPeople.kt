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
package com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.info

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.api.people.view.PeopleBar
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.state.SingleMediaItemState

@Composable
internal fun MediaItemInfoPeople(
    mediaItem: SingleMediaItemState,
    action: (com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction) -> Unit
) {
    if (mediaItem.peopleInMediaItem.isNotEmpty()) {
        PeopleBar(
            people = mediaItem.peopleInMediaItem,
            onPersonSelected = { action(com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction.PersonSelected(it)) }
        )
    }
}