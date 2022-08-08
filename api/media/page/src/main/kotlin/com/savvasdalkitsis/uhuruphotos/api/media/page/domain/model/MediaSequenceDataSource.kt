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
package com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model

sealed class MediaSequenceDataSource {

    object Single : MediaSequenceDataSource()
    object AllMedia : MediaSequenceDataSource()
    data class SearchResults(val query: String) : MediaSequenceDataSource()
    data class PersonResults(val personId: Int) : MediaSequenceDataSource()
    data class AutoAlbum(val albumId: Int) : MediaSequenceDataSource()
    data class UserAlbum(val albumId: Int) : MediaSequenceDataSource()
    data class LocalAlbum(val albumId: Int) : MediaSequenceDataSource()
    object FavouriteMedia : MediaSequenceDataSource()
    object HiddenMedia : MediaSequenceDataSource()
    object Trash : MediaSequenceDataSource()

    val toArgument : String get() = when(this) {
        Single -> "single"
        AllMedia -> "allMedia"
        is SearchResults -> "search::${query}"
        is PersonResults -> "person::${personId}"
        is AutoAlbum -> "autoAlbum::${albumId}"
        is UserAlbum -> "userAlbum::${albumId}"
        is LocalAlbum -> "localAlbum::${albumId}"
        FavouriteMedia -> "favouriteMedia"
        HiddenMedia -> "hiddenMedia"
        Trash -> "trash"
    }

    companion object {
        fun from(argument: String): MediaSequenceDataSource = when {
            argument.contains("::") -> argument.prefixed()
            argument == "favouriteMedia" -> FavouriteMedia
            argument == "hiddenMedia" -> HiddenMedia
            argument == "trash" -> Trash
            argument == "allMedia" -> AllMedia
            else -> Single
        }

        private fun String.prefixed(): MediaSequenceDataSource =
            ifPrefixIs("search") { SearchResults(it) } ?:
            ifPrefixIs("person") { PersonResults(it.toInt()) } ?:
            ifPrefixIs("autoAlbum") { AutoAlbum(it.toInt()) } ?:
            ifPrefixIs("userAlbum") { UserAlbum(it.toInt()) } ?:
            ifPrefixIs("localAlbum") { LocalAlbum(it.toInt()) } ?:
            throw IllegalArgumentException("Prefixed sequence didn't match any patterns")

        private fun String.ifPrefixIs(
            prefix: String,
            generate: (String) -> MediaSequenceDataSource,
        ): MediaSequenceDataSource? = if (startsWith("$prefix::"))
            generate(removePrefix("$prefix::"))
        else
            null
    }
}
