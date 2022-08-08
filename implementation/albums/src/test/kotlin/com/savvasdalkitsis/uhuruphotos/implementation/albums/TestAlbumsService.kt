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
package com.savvasdalkitsis.uhuruphotos.implementation.albums

import com.savvasdalkitsis.uhuruphotos.api.albums.service.AlbumsService
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.Album
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.AlbumById
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.AlbumsByDate
import io.mockk.coEvery
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun AlbumsService.respondsForAlbum(id: Int, album: Album.CompleteAlbum) {
    coEvery { getAlbum(albumId(id), 1) } returns AlbumById(
        album
    )
}

fun AlbumsService.willRespondForAlbum(id: Int, album: Album.CompleteAlbum): Mutex {
    val response = Mutex(locked = true)
    coEvery { getAlbum(albumId(id), 1) } coAnswers {
        response.withLock {
            AlbumById(album)
        }
    }
    return response
}
fun AlbumsService.respondsWith(vararg albums: Album.IncompleteAlbum) {
    coEvery { getAlbumsByDate() } returns AlbumsByDate(albums.size, listOf(*albums))
}
fun AlbumsService.willRespondWith(vararg albums: Album.IncompleteAlbum): Mutex {
    val response = Mutex(locked = true)
    coEvery { getAlbumsByDate() } coAnswers {
        response.withLock {
            AlbumsByDate(albums.size, listOf(*albums))
        }
    }
    return response
}

fun AlbumsService.willRespondForPersonWith(personId: Int, vararg albums: Album.IncompleteAlbum): Mutex {
    val response = Mutex(locked = true)
    coEvery { getAlbumsForPerson(personId) } coAnswers {
        response.withLock {
            AlbumsByDate(albums.size, listOf(*albums))
        }
    }
    return response
}

fun AlbumsService.willRespondForPersonAlbum(personId: Int, albumId: Int, album: Album.CompleteAlbum): Mutex {
    val response = Mutex(locked = true)
    coEvery { getAlbumForPerson(albumId(albumId), personId) } coAnswers {
        response.withLock {
            AlbumById(album)
        }
    }
    return response
}

fun Mutex.completes() = unlock()
