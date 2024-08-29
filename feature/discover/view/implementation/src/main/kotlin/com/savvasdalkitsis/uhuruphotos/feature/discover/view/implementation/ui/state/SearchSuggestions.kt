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
package com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state

import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.api.state.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.Person
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.search.SearchSuggestion

@Immutable
data class RecentSearchSuggestion(val query: String) : SearchSuggestion {
    override val type: String = TYPE
    override val filterable: String = query

    companion object {
        const val TYPE = "recent"
    }
}

@Immutable
data class PersonSearchSuggestion(val person: Person) : SearchSuggestion {
    override val type: String = TYPE
    override val filterable: String = person.name
    companion object {
        const val TYPE = "person"
    }
}

@Immutable
data class ServerSearchSuggestion(val query: String) : SearchSuggestion {
    override val type: String = TYPE
    override val filterable: String = query
    companion object {
        const val TYPE = "server"
    }
}

@Immutable
data class UserAlbumSearchSuggestion(val userAlbums: UserAlbums) : SearchSuggestion {
    override val type: String = TYPE
    override val filterable: String = userAlbums.title.orEmpty()
    companion object {
        const val TYPE = "userAlbum"
    }
}

@Immutable
data class AutoAlbumSearchSuggestion(val autoAlbum: AutoAlbum) : SearchSuggestion {
    override val type: String = TYPE
    override val filterable: String = autoAlbum.title
    companion object {
        const val TYPE = "autoAlbum"
    }
}
