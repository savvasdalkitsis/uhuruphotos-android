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
package com.savvasdalkitsis.uhuruphotos.implementation.photos.repository

import com.savvasdalkitsis.uhuruphotos.api.db.extensions.async
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.await
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.read
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoDetails
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoDetailsQueries
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoSummary
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoSummaryQueries
import com.savvasdalkitsis.uhuruphotos.api.photos.entities.toPhotoSummary
import com.savvasdalkitsis.uhuruphotos.api.photos.model.toPhotoDetails
import com.savvasdalkitsis.uhuruphotos.implementation.photos.service.PhotosService
import com.savvasdalkitsis.uhuruphotos.implementation.photos.worker.PhotoWorkScheduler
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val photoDetailsQueries: PhotoDetailsQueries,
    private val photoSummaryQueries: PhotoSummaryQueries,
    private val photoWorkScheduler: PhotoWorkScheduler,
    private val photosService: PhotosService,
) {

    fun observeAllPhotoDetails(): Flow<List<PhotoDetails>> =
        photoDetailsQueries.getAll(limit = -1).asFlow()
            .onStart {
                emitAll(photoDetailsQueries.getAll(limit = 100).asFlow().take(1))
            }
            .mapToList()

    fun observeFavouritePhotoSummaries(favouriteThreshold: Int): Flow<List<PhotoSummary>> =
        photoSummaryQueries.getFavourites(favouriteThreshold).asFlow()
            .mapToList()

    fun observePhotoDetails(id: String): Flow<PhotoDetails> =
        photoDetailsQueries.getPhoto(id).asFlow().mapToOneNotNull()
            .onStart {
               refreshDetailsIfMissing(id)
            }

    suspend fun getPhotoDetails(id: String): PhotoDetails? =
        photoDetailsQueries.getPhoto(id).awaitSingleOrNull()

    suspend fun getFavouritePhotoSummaries(favouriteThreshold: Int): List<PhotoSummary> =
        photoSummaryQueries.getFavourites(favouriteThreshold).await()

    suspend fun refreshDetailsIfMissing(id: String) {
        when (getPhotoDetails(id)) {
            null -> {
                refreshDetails(id)
            }
        }
    }

    suspend fun refreshDetailsNowIfMissing(id: String) {
        when (getPhotoDetails(id)) {
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

    suspend fun refreshFavourites(favouriteThreshold: Int) {
        val currentFavouriteIds = photoSummaryQueries.getFavourites(favouriteThreshold)
            .await()
            .map { it.id }
            .toSet()
        val newFavourites = photosService.getFavouritePhotos().results.flatMap { it.items }
        val newFavouriteIds = newFavourites.map { it.id }.toSet()
        newFavourites.forEach {
            async { photoSummaryQueries.setRating(it.rating, it.id) }
        }
        (currentFavouriteIds - newFavouriteIds).forEach {
            val rating = photosService.getPhoto(it).rating
            async { photoSummaryQueries.setRating(rating, it) }
        }
    }

    suspend fun insertPhoto(photoDetails: PhotoDetails) {
        async {
            photoDetailsQueries.insert(photoDetails)
            photoSummaryQueries.setRating(photoDetails.rating, photoDetails.imageHash)
        }
    }

    suspend fun setPhotoRating(id: String, rating: Int) {
        async {
            photoDetailsQueries.setRating(rating, id)
            photoSummaryQueries.setRating(rating, id)
        }
    }

    suspend fun deletePhoto(id: String) {
        async {
            photoDetailsQueries.delete(id)
            photoSummaryQueries.delete(id)
        }
    }

}
