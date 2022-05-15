package com.savvasdalkitsis.uhuruphotos.photos.model

sealed class PhotoSequenceDataSource {

    object Single : PhotoSequenceDataSource()
    object AllPhotos : PhotoSequenceDataSource()
    data class SearchResults(val query: String) : PhotoSequenceDataSource()
    data class PersonResults(val personId: Int) : PhotoSequenceDataSource()

    val toArgument : String get() = when(this) {
        Single -> "single"
        AllPhotos -> "allPhotos"
        is SearchResults -> "search::${query}"
        is PersonResults -> "person::${personId}"
    }

    companion object {
        fun from(argument: String) = when {
            argument.startsWith("search::") ->
                SearchResults(argument.removePrefix("search::"))
            argument.startsWith("person::") ->
                PersonResults(argument.removePrefix("person::").toInt())
            argument == "allPhotos" -> AllPhotos
            else -> Single
        }
    }
}
