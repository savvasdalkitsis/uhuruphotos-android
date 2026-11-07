/*
Copyright 2024 Savvas Dalkitsis

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

        @Composable
        fun stats() =
            SharedElementId("stats")

        @Composable
        fun statsText() =
            SharedElementId("stats-text")

        @Composable
        fun mediaWithoutDate() =
            SharedElementId("media-without-date")

        @Composable
        fun mediaWithoutDateText() =
            SharedElementId("media-without-date-text")

        @Composable
        fun videos() =
            SharedElementId("videos")

        @Composable
        fun videosText() =
            SharedElementId("videos-text")
    }
}