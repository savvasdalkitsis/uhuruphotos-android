package com.savvasdalkitsis.librephotos.photos.usecase

import com.savvasdalkitsis.librephotos.albums.api.AlbumsService
import com.savvasdalkitsis.librephotos.photos.api.PhotosService
import com.savvasdalkitsis.librephotos.photos.api.model.PhotoResult
import com.savvasdalkitsis.librephotos.photos.model.Photos
import com.savvasdalkitsis.librephotos.server.usecase.ServerUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PhotosUseCase @Inject constructor(
//    private val photosStore: Store<Unit, Photos>,
    private val photosService: PhotosService,
    private val albumsService: AlbumsService,
    private val serverUseCase: ServerUseCase,
){

    fun getPhotos(): Flow<Photos> = flow {
        val albums = albumsService.getAlbumsByDate().results
        val serverUrl = serverUseCase.getServerUrl()
        val photos = mutableListOf<String>()
        albums.forEach { incompleteAlbum ->
            val albumById = albumsService.getAlbum(incompleteAlbum.id)
            albumById.results.items.forEach { albumItem ->
                photos += serverUrl + photosService.getPhoto(albumItem.url).bigThumbnailUrl.removeSuffix(".webp")
                emit(Photos(listOf(*photos.toTypedArray())))
            }
        }
        emit(Photos(listOf(*photos.toTypedArray()), finished = true))
    }
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