package com.savvasdalkitsis.librephotos.photos.usecase

import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.savvasdalkitsis.librephotos.photos.api.PhotosService
import com.savvasdalkitsis.librephotos.photos.model.Photos
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class PhotosUseCase @Inject constructor(
//    private val photosStore: Store<Unit, Photos>,
    private val photosService: PhotosService,
){

    suspend fun getPhotos(): Photos = Photos(photosService.getPhotos().results.map {
        it.bigThumbnailUrl.removeSuffix(".webp")
    })
//        photosStore.stream(StoreRequest.cached(Unit,  false))
//        .mapNotNull {
//            when (it) {
//                is StoreResponse.Loading -> null
//                is StoreResponse.Data -> it.value
//                is StoreResponse.NoNewData -> null
//                is StoreResponse.Error.Exception -> null
//                is StoreResponse.Error.Message -> null
//            }
//        }

}