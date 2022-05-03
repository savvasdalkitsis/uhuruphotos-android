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
package com.savvasdalkitsis.uhuruphotos.person.viewmodel

import com.savvasdalkitsis.uhuruphotos.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.db.people.People
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplay

sealed class PersonMutation {
    object Loading : PersonMutation()
    data class ShowPersonPhotos(val albums: List<Album>) : PersonMutation()
    data class ShowPersonDetails(val person: People) : PersonMutation()
    data class SetFeedDisplay(val display: FeedDisplay) : PersonMutation()
}
