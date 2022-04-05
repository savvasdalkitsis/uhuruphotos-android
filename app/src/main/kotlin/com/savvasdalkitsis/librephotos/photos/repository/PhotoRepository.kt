package com.savvasdalkitsis.librephotos.photos.repository

import com.savvasdalkitsis.librephotos.extensions.awaitSingleOrNull
import com.savvasdalkitsis.librephotos.extensions.crud
import com.savvasdalkitsis.librephotos.photos.api.PhotosService
import com.savvasdalkitsis.librephotos.photos.api.model.toPhotoDetails
import com.savvasdalkitsis.librephotos.photos.db.PhotoDetails
import com.savvasdalkitsis.librephotos.photos.db.PhotoDetailsQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val photoDetailsQueries: PhotoDetailsQueries,
    private val photosService: PhotosService,
) {

    fun getPhoto(id: String): Flow<PhotoDetails> =
        photoDetailsQueries.getPhoto(id).asFlow().mapToOneNotNull()
            .onStart {
                when (photoDetailsQueries.getPhoto(id).awaitSingleOrNull()) {
                    null -> photosService.getPhoto(id).toPhotoDetails().apply {
                        crud { photoDetailsQueries.insert(this) }
                    }
                }
            }

}
