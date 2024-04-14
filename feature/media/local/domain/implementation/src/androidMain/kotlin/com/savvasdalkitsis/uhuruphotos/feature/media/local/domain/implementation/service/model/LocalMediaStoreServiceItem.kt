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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model

import android.net.Uri

sealed class LocalMediaStoreServiceItem(
    open val id: Long,
    open val displayName: String,
    open val dateTaken: Long,
    open val bucketId: Int,
    open val bucketName: String,
    open val width: Int?,
    open val height: Int?,
    open val size: Int?,
    open val contentUri: Uri,
    open val path: String,
    open val orientation: String?,
) {

    data class Photo(
        override val id: Long,
        override val displayName: String,
        override val dateTaken: Long,
        override val bucketId: Int,
        override val bucketName: String,
        override val width: Int?,
        override val height: Int?,
        override val size: Int?,
        override val contentUri: Uri,
        override val path: String,
        override val orientation: String?,
    ): LocalMediaStoreServiceItem(
        id, displayName, dateTaken, bucketId, bucketName, width, height, size, contentUri, path, orientation
    )

    data class Video(
        override val id: Long,
        override val displayName: String,
        override val dateTaken: Long,
        override val bucketId: Int,
        override val bucketName: String,
        override val width: Int?,
        override val height: Int?,
        override val size: Int?,
        val duration: Int?,
        override val contentUri: Uri,
        override val path: String,
        override val orientation: String?,
    ): LocalMediaStoreServiceItem(
        id, displayName, dateTaken, bucketId, bucketName, width, height, size, contentUri, path, orientation
    )

}
