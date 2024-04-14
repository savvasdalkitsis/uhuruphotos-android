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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class LightboxSequenceDataSource(
    val showMediaSyncState: Boolean = false,
    val shouldShowDeleteButton: Boolean = false,
) : Parcelable {

    @Parcelize
    data object Single : LightboxSequenceDataSource()
    @Parcelize
    data object Feed : LightboxSequenceDataSource(
        showMediaSyncState = true,
        shouldShowDeleteButton = true,
    )
    @Parcelize
    data class Memory(val yearsAgo: Int) : LightboxSequenceDataSource(
        showMediaSyncState = true,
        shouldShowDeleteButton = true,
    )
    @Parcelize
    data class SearchResults(val query: String) : LightboxSequenceDataSource()
    @Parcelize
    data class PersonResults(val personId: Int) : LightboxSequenceDataSource()
    @Parcelize
    data class AutoAlbum(val albumId: Int) : LightboxSequenceDataSource()
    @Parcelize
    data class UserAlbum(val albumId: Int) : LightboxSequenceDataSource()
    @Parcelize
    data class LocalAlbum(val albumId: Int) : LightboxSequenceDataSource(
        shouldShowDeleteButton = true,
    )
    @Parcelize
    data object FavouriteMedia : LightboxSequenceDataSource()
    @Parcelize
    data object HiddenMedia : LightboxSequenceDataSource()
    @Parcelize
    data object Trash : LightboxSequenceDataSource()
    @Parcelize
    data object Videos : LightboxSequenceDataSource()
    @Parcelize
    data object Undated : LightboxSequenceDataSource()
}
