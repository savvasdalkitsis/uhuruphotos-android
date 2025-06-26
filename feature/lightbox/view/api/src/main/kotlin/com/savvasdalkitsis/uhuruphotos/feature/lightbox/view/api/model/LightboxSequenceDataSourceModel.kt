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
import kotlinx.serialization.Serializable

@Serializable
sealed class LightboxSequenceDataSourceModel(
    val showMediaSyncState: Boolean = false,
    val shouldShowDeleteButton: Boolean = false,
) : Parcelable {

    @Parcelize
    @Serializable
    data object SingleItemModel : LightboxSequenceDataSourceModel()
    @Parcelize
    @Serializable
    data object FeedModel : LightboxSequenceDataSourceModel(
        showMediaSyncState = true,
        shouldShowDeleteButton = true,
    )
    @Parcelize
    @Serializable
    data class MemoryModel(val yearsAgo: Int) : LightboxSequenceDataSourceModel(
        showMediaSyncState = true,
        shouldShowDeleteButton = true,
    )
    @Parcelize
    @Serializable
    data class SearchResultsModel(val query: String) : LightboxSequenceDataSourceModel()
    @Parcelize
    @Serializable
    data class PersonResultsModel(val personId: Int) : LightboxSequenceDataSourceModel()
    @Parcelize
    @Serializable
    data class AutoAlbumModel(val albumId: Int) : LightboxSequenceDataSourceModel()
    @Parcelize
    @Serializable
    data class UserAlbumModel(val albumId: Int) : LightboxSequenceDataSourceModel()
    @Parcelize
    @Serializable
    data class LocalAlbumModel(val albumId: Int) : LightboxSequenceDataSourceModel(
        shouldShowDeleteButton = true,
    )
    @Parcelize
    @Serializable
    data object FavouriteMediaModel : LightboxSequenceDataSourceModel()
    @Parcelize
    @Serializable
    data object HiddenMediaModel : LightboxSequenceDataSourceModel()
    @Parcelize
    @Serializable
    data object TrashModel : LightboxSequenceDataSourceModel()
    @Parcelize
    @Serializable
    data object VideosModel : LightboxSequenceDataSourceModel()
    @Parcelize
    @Serializable
    data object UndatedModel : LightboxSequenceDataSourceModel()
}
