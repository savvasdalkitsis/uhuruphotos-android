package com.savvasdalkitsis.uhuruphotos.search.view.state

import com.savvasdalkitsis.uhuruphotos.people.api.view.state.Person

sealed class SearchSuggestion(
    val filterable: String,
) {

    data class RecentSearchSuggestion(val query: String) : SearchSuggestion(query)
    data class PersonSearchSuggestion(val person: Person) : SearchSuggestion(person.name)
    data class ServerSearchSuggestion(val query: String) : SearchSuggestion(query)
}
