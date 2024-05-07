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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model

import androidx.compose.runtime.Immutable
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.DOWNLOADING
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.LOCAL_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.PROCESSING
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.REMOTE_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.SYNCED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.UPLOADING
import java.io.Serializable

@Immutable
sealed class MediaId<T : Serializable> : Parcelable {

    fun matches(id: MediaId<*>) =
        this == id || preferLocal == id.preferLocal || preferRemote == id.preferRemote

    abstract val value: T
    abstract val isVideo: Boolean
    abstract fun thumbnailUri(serverUrl: String?): String
    abstract fun fullResUri(serverUrl: String?): String
    abstract val syncState: MediaItemSyncState

    abstract val preferRemote: MediaId<*>
    abstract val findRemote: Remote?
    abstract val preferLocal: MediaId<*>
    abstract val findLocals: Set<Local>
    abstract val serializableId: String

    val isBothRemoteAndLocal: Boolean get() = findLocals.isNotEmpty() && findRemote != null

    @Parcelize
    data class Remote(
        override val value: String,
        override val isVideo: Boolean,
    ): MediaId<String>() {
        override val preferRemote get() = this
        override val preferLocal get() = this
        override val findRemote get() = this
        override val findLocals: Set<Local> get() = emptySet()
        override val serializableId: String get() = "remote:$value"
        override val syncState: MediaItemSyncState get() = REMOTE_ONLY

        override fun fullResUri(serverUrl: String?): String =
            serverUrl?.let { value.toFullSizeUrlFromId(isVideo, it) } ?: ""
        override fun thumbnailUri(serverUrl: String?): String =
            serverUrl?.let { value.toThumbnailUrlFromId(it) } ?: ""

        fun toDownloading() = Downloading(value, isVideo)
    }

    @Parcelize
    data class Downloading(
        override val value: String,
        override val isVideo: Boolean,
    ): MediaId<String>() {
        val remote get() = Remote(value, isVideo)

        override val preferRemote get() = remote
        override val preferLocal get() = this
        override val findRemote get() = remote
        override val findLocals: Set<Local> get() = emptySet()
        override val serializableId: String get() = "downloading:$value"
        override val syncState: MediaItemSyncState get() = DOWNLOADING

        override fun fullResUri(serverUrl: String?): String = remote.fullResUri(serverUrl)
        override fun thumbnailUri(serverUrl: String?): String = remote.thumbnailUri(serverUrl)
    }

    @Parcelize
    data class Uploading(
        override val value: Long,
        val folderId: Int,
        override val isVideo: Boolean,
        val contentUri: String,
        val thumbnailUri: String,
    ): MediaId<Long>() {
        val local get() = Local(value, folderId, isVideo, contentUri, thumbnailUri)
        override val preferRemote get() = this
        override val preferLocal get() = local
        override val findRemote get() = null
        override val findLocals: Set<Local> get() = setOf(local)
        override val serializableId: String get() = "uploading:$value"
        override val syncState: MediaItemSyncState get() = UPLOADING

        override fun fullResUri(serverUrl: String?): String = local.fullResUri(serverUrl)
        override fun thumbnailUri(serverUrl: String?): String = thumbnailUri
    }


    @Parcelize
    data class Processing(
        override val value: Long,
        val folderId: Int,
        override val isVideo: Boolean,
        val contentUri: String,
        val thumbnailUri: String,
    ): MediaId<Long>() {
        val local get() = Local(value, folderId, isVideo, contentUri, thumbnailUri)
        override val preferRemote get() = this
        override val preferLocal get() = local
        override val findRemote get() = null
        override val findLocals: Set<Local> get() = setOf(local)
        override val serializableId: String get() = "processing:$value"
        override val syncState: MediaItemSyncState get() = PROCESSING

        override fun fullResUri(serverUrl: String?): String = local.fullResUri(serverUrl)
        override fun thumbnailUri(serverUrl: String?): String = thumbnailUri
    }

    @Parcelize
    data class Local(
        override val value: Long,
        val folderId: Int,
        override val isVideo: Boolean,
        val contentUri: String,
        val thumbnailUri: String,
    ): MediaId<Long>() {
        override val preferRemote get() = this
        override val preferLocal get() = this
        override val findRemote get() = null
        override val findLocals: Set<Local> get() = setOf(this)
        override val serializableId: String get() = "local:$value"
        override val syncState: MediaItemSyncState get() = LOCAL_ONLY

        override fun fullResUri(serverUrl: String?): String = contentUri
        override fun thumbnailUri(serverUrl: String?): String = thumbnailUri

        fun toUploading() = Uploading(value, folderId, isVideo, contentUri, thumbnailUri)
        fun toProcessing() = Processing(value, folderId, isVideo, contentUri, thumbnailUri)
    }

    @Suppress("DataClassPrivateConstructor")
    @Parcelize
    data class Group private constructor(
        override val value: ArrayList<MediaId<*>>,
        override val isVideo: Boolean,
    ): MediaId<ArrayList<MediaId<*>>>() {
        override val findRemote: Remote? get() = value.firstNotNullOfOrNull { it.findRemote }
        override val findLocals: Set<Local> get() = value.flatMap { it.findLocals }.toSet()
        override val preferRemote: MediaId<*> get() = findRemote ?: value.first()
        override val preferLocal: MediaId<*> get() = findLocals.firstOrNull() ?: value.first()
        override val serializableId: String get() =
            "group:${value.joinToString(",") { it.serializableId }}"
        override val syncState: MediaItemSyncState get() = when {
            value.any { it is Downloading } -> DOWNLOADING
            value.any { it is Uploading } -> UPLOADING
            findRemote == null -> LOCAL_ONLY
            findLocals.isEmpty() -> REMOTE_ONLY
            else -> SYNCED
        }
        override fun fullResUri(serverUrl: String?): String = preferLocal.fullResUri(serverUrl)
        override fun thumbnailUri(serverUrl: String?): String = if (isVideo)
            preferRemote.thumbnailUri(serverUrl)
        else
            preferLocal.thumbnailUri(serverUrl)

        companion object {
            operator fun invoke(
                value: Collection<MediaId<*>>,
                isVideo: Boolean,
            ) =
                Group(ArrayList(value.distinct()), isVideo)
        }
    }
}

private fun String.toThumbnailUrlFromId(serverUrl: String): String =
    "/media/square_thumbnails/$this".toAbsoluteRemoteUrl(serverUrl)

private fun String.toFullSizeUrlFromId(isVideo: Boolean, serverUrl: String): String = when {
    isVideo -> "/media/video/$this".toAbsoluteRemoteUrl(serverUrl)
    else -> "/media/photos/$this".toAbsoluteRemoteUrl(serverUrl)
}

private fun String.toAbsoluteRemoteUrl(serverUrl: String): String = this
    .removeSuffix(".webp")
    .removeSuffix(".mp4")
    .let { serverUrl + it }