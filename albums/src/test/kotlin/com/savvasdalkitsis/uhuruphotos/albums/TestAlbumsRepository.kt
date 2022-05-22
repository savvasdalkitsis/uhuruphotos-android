package com.savvasdalkitsis.uhuruphotos.albums

import com.savvasdalkitsis.uhuruphotos.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.db.albums.GetAlbums
import com.savvasdalkitsis.uhuruphotos.db.albums.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.Group
import io.mockk.coEvery

fun AlbumsRepository.reportsHavingNoAlbums() {
    coEvery { hasAlbums() } returns false
}

fun AlbumsRepository.reportsHavingAlbums() {
    coEvery { hasAlbums() } returns true
}

fun AlbumsRepository.returnsAlbums(vararg albums: Pair<String, List<GetAlbums>>) {
    coEvery { getAlbumsByDate() } returns Group(albums.toMap())
}

fun AlbumsRepository.returnsAlbumWithEntries(vararg albums: GetAlbums) {
    coEvery { getAlbumsByDate() } returns Group(mapOf("albumId" to albums.toList()))
}

fun AlbumsRepository.returnsPersonAlbumWithEntries(personId: Int, vararg albums: GetPersonAlbums) {
    coEvery { getPersonAlbums(personId) } returns Group(mapOf("albumId" to albums.toList()))
}