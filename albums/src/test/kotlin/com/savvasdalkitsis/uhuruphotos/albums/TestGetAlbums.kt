package com.savvasdalkitsis.uhuruphotos.albums

import com.savvasdalkitsis.uhuruphotos.albums.TestAlbums.*
import com.savvasdalkitsis.uhuruphotos.db.albums.GetAlbums
import com.savvasdalkitsis.uhuruphotos.db.albums.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoSummary

val serverAlbumLocation = "serverAlbumLocation"

fun albumId(id: Int) = "album$id"

fun album(id: Int) = album.copy(id = albumId(id), date = "2000-01-0$id")

fun incompleteAlbum(id: Int) = incompleteAlbum.copy(
    id = album(id).id,
    date = album(id).date
)

fun completeAlbum(id: Int) = completeAlbum.copy(
    id = album(id).id,
    date = album(id).date
)

fun album(album: Int, vararg albums: GetPersonAlbums) = albumId(album) to albums.map {
    it.copy(id = albumId(album), albumDate = album(album).date)
}

fun album(album: Int, vararg albums: GetAlbums) = albumId(album) to albums.map {
    it.copy(id = albumId(album), albumDate = album(album).date)
}

fun entry(photoSummary: PhotoSummary) = TestGetAlbums.getAlbum.copy(photoId = photoSummary.id)

fun entry(personId: Int, photoSummary: PhotoSummary) =
    TestGetAlbums.getPersonAlbum.copy(personId = personId, photoId = photoSummary.id)

fun GetPersonAlbums.withServerResponseData() = copy(
    dominantColor = "",
    rating = 0,
    aspectRatio = 1f,
    type = "",
    albumLocation = serverAlbumLocation,
)

fun GetAlbums.withServerResponseData() = copy(
    dominantColor = "",
    rating = 0,
    aspectRatio = 1f,
    type = "",
    albumLocation = serverAlbumLocation,
)
