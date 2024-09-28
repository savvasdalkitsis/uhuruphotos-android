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

sealed class LightboxSequenceDataSourceModel(
    val showMediaSyncState: Boolean = false,
    val shouldShowDeleteButton: Boolean = false,
) : Parcelable {

    @Parcelize
    data object SingleItemModel : LightboxSequenceDataSourceModel()
    @Parcelize
    data object FeedModel : LightboxSequenceDataSourceModel(
        showMediaSyncState = true,
        shouldShowDeleteButton = true,
    )
    @Parcelize
    data class MemoryModel(val yearsAgo: Int) : LightboxSequenceDataSourceModel(
        showMediaSyncState = true,
        shouldShowDeleteButton = true,
    )
    @Parcelize
    data class SearchResultsModel(val query: String) : LightboxSequenceDataSourceModel()
    @Parcelize
    data class PersonResultsModel(val personId: Int) : LightboxSequenceDataSourceModel()
    @Parcelize
    data class AutoAlbumModel(val albumId: Int) : LightboxSequenceDataSourceModel()
    @Parcelize
    data class UserAlbumModel(val albumId: Int) : LightboxSequenceDataSourceModel()
    @Parcelize
    data class LocalAlbumModel(val albumId: Int) : LightboxSequenceDataSourceModel(
        shouldShowDeleteButton = true,
    )
    @Parcelize
    data object FavouriteMediaModel : LightboxSequenceDataSourceModel()
    @Parcelize
    data object HiddenMediaModel : LightboxSequenceDataSourceModel()
    @Parcelize
    data object TrashModel : LightboxSequenceDataSourceModel()
    @Parcelize
    data object VideosModel : LightboxSequenceDataSourceModel()
    @Parcelize
    data object UndatedModel : LightboxSequenceDataSourceModel()
}
