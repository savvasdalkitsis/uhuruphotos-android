package com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api

import androidx.compose.runtime.Composable

@JvmInline
value class SharedElementId(val value: String) {
    companion object {

        @Composable
        fun image(imageHash: String) =
            SharedElementId("image-$imageHash")

        @Composable
        fun imageCanvas(imageHash: String) =
            SharedElementId("image-canvas-$imageHash")

        @Composable
        fun personImage(personId: Int) =
            SharedElementId("person-image-$personId")

        @Composable
        fun personName(personId: Int) =
            SharedElementId("person-name-$personId")

        @Composable
        fun localAlbum(albumId: Int) =
            SharedElementId("local-album-$albumId")

        @Composable
        fun localAlbumTitle(albumId: Int) =
            SharedElementId("local-album-title-$albumId")

        fun autoAlbumsCanvas() =
            SharedElementId("auto-albums-canvas")

        fun autoAlbumsTitle() =
            SharedElementId("auto-albums-title")

        fun favouriteMediaCanvas() =
            SharedElementId("favourite-media-canvas")

        fun favouriteMediaTitle() =
            SharedElementId("favourite-media-title")

        fun userAlbumsCanvas() =
            SharedElementId("user-albums-canvas")

        fun userAlbumsTitle() =
            SharedElementId("user-albums-title")

        fun trash() =
            SharedElementId("trash")

        fun trashTitle() =
            SharedElementId("trash-title")

        fun hidden() =
            SharedElementId("hidden")

        fun hiddenTitle() =
            SharedElementId("hidden-title")
    }
}