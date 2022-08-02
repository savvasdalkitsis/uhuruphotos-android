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
package com.savvasdalkitsis.uhuruphotos.api.photos.model

sealed class PhotoSequenceDataSource {

    object Single : PhotoSequenceDataSource()
    object AllPhotos : PhotoSequenceDataSource()
    data class SearchResults(val query: String) : PhotoSequenceDataSource()
    data class PersonResults(val personId: Int) : PhotoSequenceDataSource()
    data class AutoAlbum(val albumId: Int) : PhotoSequenceDataSource()
    data class UserAlbum(val albumId: Int) : PhotoSequenceDataSource()
    data class LocalAlbum(val albumId: Int) : PhotoSequenceDataSource()
    object FavouritePhotos : PhotoSequenceDataSource()
    object HiddenPhotos : PhotoSequenceDataSource()
    object Trash : PhotoSequenceDataSource()

    val toArgument : String get() = when(this) {
        Single -> "single"
        AllPhotos -> "allPhotos"
        is SearchResults -> "search::${query}"
        is PersonResults -> "person::${personId}"
        is AutoAlbum -> "autoAlbum::${albumId}"
        is UserAlbum -> "userAlbum::${albumId}"
        is LocalAlbum -> "localAlbum::${albumId}"
        FavouritePhotos -> "favouritePhotos"
        HiddenPhotos -> "hiddenPhotos"
        Trash -> "trash"
    }

    companion object {
        fun from(argument: String): PhotoSequenceDataSource = when {
            argument.contains("::") -> argument.prefixed()
            argument == "favouritePhotos" -> FavouritePhotos
            argument == "hiddenPhotos" -> HiddenPhotos
            argument == "trash" -> Trash
            argument == "allPhotos" -> AllPhotos
            else -> Single
        }

        private fun String.prefixed(): PhotoSequenceDataSource =
            ifPrefixIs("search") { SearchResults(it) } ?:
            ifPrefixIs("person") { PersonResults(it.toInt()) } ?:
            ifPrefixIs("autoAlbum") { AutoAlbum(it.toInt()) } ?:
            ifPrefixIs("userAlbum") { UserAlbum(it.toInt()) } ?:
            ifPrefixIs("localAlbum") { LocalAlbum(it.toInt()) } ?:
            throw IllegalArgumentException("Prefixed sequence didn't match any patterns")

        private fun String.ifPrefixIs(
            prefix: String,
            generate: (String) -> PhotoSequenceDataSource,
        ): PhotoSequenceDataSource? = if (startsWith("$prefix::"))
            generate(removePrefix("$prefix::"))
        else
            null
    }
}
