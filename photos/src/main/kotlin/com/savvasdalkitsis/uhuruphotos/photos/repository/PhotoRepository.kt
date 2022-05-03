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
package com.savvasdalkitsis.uhuruphotos.photos.repository

import com.savvasdalkitsis.uhuruphotos.db.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.db.extensions.crud
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoDetails
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoDetailsQueries
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoSummaryQueries
import com.savvasdalkitsis.uhuruphotos.photos.service.PhotosService
import com.savvasdalkitsis.uhuruphotos.photos.service.model.toPhotoDetails
import com.savvasdalkitsis.uhuruphotos.photos.worker.PhotoWorkScheduler
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val photoDetailsQueries: PhotoDetailsQueries,
    private val photoSummaryQueries: PhotoSummaryQueries,
    private val photoWorkScheduler: PhotoWorkScheduler,
    private val photosService: PhotosService,
) {

    fun getAllPhotos(): Flow<List<PhotoDetails>> = photoDetailsQueries.getAll()
        .asFlow().mapToList()

    fun getPhoto(id: String): Flow<PhotoDetails> =
        photoDetailsQueries.getPhoto(id).asFlow().mapToOneNotNull()
            .onStart {
               refreshDetailsIfMissing(id)
            }

    suspend fun refreshDetailsIfMissing(id: String) {
        when (photoDetailsQueries.getPhoto(id).awaitSingleOrNull()) {
            null -> {
                refreshDetails(id)
            }
        }
    }

    suspend fun refreshDetailsNowIfMissing(id: String) {
        when (photoDetailsQueries.getPhoto(id).awaitSingleOrNull()) {
            null -> {
                refreshDetailsNow(id)
            }
        }
    }

    suspend fun refreshDetailsNow(id: String) {
        photosService.getPhoto(id).toPhotoDetails().apply {
            insertPhoto(this)
        }
    }

    fun refreshDetails(id: String) {
        photoWorkScheduler.schedulePhotoDetailsRetrieve(id)
    }

    suspend fun insertPhoto(photoDetails: PhotoDetails) {
        crud {
            photoDetailsQueries.insert(photoDetails)
            photoSummaryQueries.setRating(photoDetails.rating, photoDetails.imageHash)
        }
    }

    suspend fun setPhotoRating(id: String, rating: Int) {
        crud {
            photoDetailsQueries.setRating(rating, id)
            photoSummaryQueries.setRating(rating, id)
        }
    }

    suspend fun deletePhoto(id: String) {
        crud {
            photoDetailsQueries.delete(id)
            photoSummaryQueries.delete(id)
        }
    }

}
