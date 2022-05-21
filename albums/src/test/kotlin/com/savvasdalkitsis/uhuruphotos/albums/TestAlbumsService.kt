package com.savvasdalkitsis.uhuruphotos.albums

import com.savvasdalkitsis.uhuruphotos.albums.service.AlbumsService
import com.savvasdalkitsis.uhuruphotos.albums.service.model.Album
import com.savvasdalkitsis.uhuruphotos.albums.service.model.AlbumById
import com.savvasdalkitsis.uhuruphotos.albums.service.model.AlbumsByDate
import io.mockk.coEvery
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun AlbumsService.respondsForAlbum(id: Int, album: Album.CompleteAlbum) {
    coEvery { getAlbum(albumId(id)) } returns AlbumById(
        album
    )
}

fun AlbumsService.willRespondForAlbum(id: Int, album: Album.CompleteAlbum): Mutex {
    val response = Mutex(locked = true)
    coEvery { getAlbum(albumId(id)) } coAnswers {
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
