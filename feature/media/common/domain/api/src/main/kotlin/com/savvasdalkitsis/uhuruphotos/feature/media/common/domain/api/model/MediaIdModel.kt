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

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncStateModel.DOWNLOADING
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncStateModel.LOCAL_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncStateModel.PROCESSING
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncStateModel.REMOTE_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncStateModel.SYNCED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncStateModel.UPLOADING
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Immutable
sealed class MediaIdModel<T : Serializable> private constructor(
) : Parcelable {

    fun matches(id: MediaIdModel<*>) =
        this == id || preferLocal == id.preferLocal || preferRemote == id.preferRemote

    abstract val value: T
    abstract val isVideo: Boolean
    abstract fun thumbnailUri(serverUrl: String?): String
    abstract fun fullResUri(serverUrl: String?): String
    abstract val syncState: MediaItemSyncStateModel

    abstract val preferRemote: MediaIdModel<*>
    abstract val findRemote: RemoteIdModel?
    abstract val preferLocal: MediaIdModel<*>
    abstract val findLocals: Set<LocalIdModel>
    abstract val serializableId: String

    val hasRemote: Boolean get() = findRemote != null
    val isBothRemoteAndLocal: Boolean get() = findLocals.isNotEmpty() && hasRemote

    @Parcelize
    data class RemoteIdModel(
        override val value: String,
        override val isVideo: Boolean,
    ): MediaIdModel<String>() {
        @IgnoredOnParcel
        @Transient
        override val preferRemote = this
        @IgnoredOnParcel
        @Transient
        override val preferLocal = this
        @IgnoredOnParcel
        @Transient
        override val findRemote = this
        @IgnoredOnParcel
        @Transient
        override val findLocals: Set<LocalIdModel> = emptySet()
        @IgnoredOnParcel
        @Transient
        override val serializableId: String = "remote:$value"

        @IgnoredOnParcel
        override val syncState: MediaItemSyncStateModel = REMOTE_ONLY
        override fun fullResUri(serverUrl: String?): String =
            serverUrl?.let { value.toFullSizeUrlFromId(isVideo, it) } ?: ""
        override fun thumbnailUri(serverUrl: String?): String =
            serverUrl?.let { value.toThumbnailUrlFromId(it) } ?: ""

        fun toDownloading() = DownloadingIdModel(value, isVideo)
    }

    @Parcelize
    data class DownloadingIdModel(
        override val value: String,
        override val isVideo: Boolean,
    ): MediaIdModel<String>() {
        @IgnoredOnParcel
        val remote get() = RemoteIdModel(value, isVideo)

        @IgnoredOnParcel
        @Transient
        override val preferRemote = remote
        @IgnoredOnParcel
        @Transient
        override val preferLocal = this
        @IgnoredOnParcel
        @Transient
        override val findRemote = remote
        @IgnoredOnParcel
        @Transient
        override val findLocals: Set<LocalIdModel> = emptySet()

        @IgnoredOnParcel
        @Transient
        override val serializableId: String = "downloading:$value"

        @IgnoredOnParcel
        override val syncState: MediaItemSyncStateModel = DOWNLOADING
        override fun fullResUri(serverUrl: String?): String = remote.fullResUri(serverUrl)
        override fun thumbnailUri(serverUrl: String?): String = remote.thumbnailUri(serverUrl)
    }

    @Parcelize
    data class UploadingIdModel(
        override val value: Long,
        val folderId: Int,
        override val isVideo: Boolean,
        val contentUri: String,
        val thumbnailUri: String,
    ): MediaIdModel<Long>() {
        @IgnoredOnParcel
        val local get() = LocalIdModel(value, folderId, isVideo, contentUri, thumbnailUri)

        @IgnoredOnParcel
        @Transient
        override val preferRemote = this
        @IgnoredOnParcel
        @Transient
        override val preferLocal = local
        @IgnoredOnParcel
        @Transient
        override val findRemote = null
        @IgnoredOnParcel
        @Transient
        override val findLocals: Set<LocalIdModel> = setOf(local)
        @IgnoredOnParcel
        @Transient
        override val serializableId: String = "uploading:$value"

        @IgnoredOnParcel
        override val syncState: MediaItemSyncStateModel = UPLOADING
        override fun fullResUri(serverUrl: String?): String = local.fullResUri(serverUrl)
        override fun thumbnailUri(serverUrl: String?): String = thumbnailUri
    }

    @Parcelize
    data class ProcessingIdModel(
        override val value: Long,
        val folderId: Int,
        override val isVideo: Boolean,
        val contentUri: String,
        val thumbnailUri: String,
    ): MediaIdModel<Long>() {
        @IgnoredOnParcel
        val local get() = LocalIdModel(value, folderId, isVideo, contentUri, thumbnailUri)

        @IgnoredOnParcel
        @Transient
        override val preferRemote = this
        @IgnoredOnParcel
        @Transient
        override val preferLocal = local
        @IgnoredOnParcel
        @Transient
        override val findRemote = null
        @IgnoredOnParcel
        @Transient
        override val findLocals: Set<LocalIdModel> = setOf(local)
        @IgnoredOnParcel
        @Transient
        override val serializableId: String = "processing:$value"

        @IgnoredOnParcel
        override val syncState: MediaItemSyncStateModel = PROCESSING

        override fun fullResUri(serverUrl: String?): String = local.fullResUri(serverUrl)
        override fun thumbnailUri(serverUrl: String?): String = thumbnailUri
    }

    @Parcelize
    data class LocalIdModel(
        override val value: Long,
        val folderId: Int,
        override val isVideo: Boolean,
        val contentUri: String,
        val thumbnailUri: String,
    ): MediaIdModel<Long>() {
        @IgnoredOnParcel
        @Transient
        override val preferRemote = this
        @IgnoredOnParcel
        @Transient
        override val preferLocal = this
        @IgnoredOnParcel
        @Transient
        override val findRemote = null
        @IgnoredOnParcel
        @Transient
        override val findLocals: Set<LocalIdModel> = setOf(this)
        @IgnoredOnParcel
        @Transient
        override val serializableId: String = "local:$value"

        @IgnoredOnParcel
        override val syncState: MediaItemSyncStateModel = LOCAL_ONLY
        override fun fullResUri(serverUrl: String?): String = contentUri
        override fun thumbnailUri(serverUrl: String?): String = thumbnailUri

        fun toUploading() = UploadingIdModel(value, folderId, isVideo, contentUri, thumbnailUri)
        fun toProcessing() = ProcessingIdModel(value, folderId, isVideo, contentUri, thumbnailUri)
    }

    @Suppress("DataClassPrivateConstructor")
    @Parcelize
    data class GroupIdModel private constructor(
        override val value: ArrayList<MediaIdModel<*>>,
        override val isVideo: Boolean,
    ): MediaIdModel<ArrayList<MediaIdModel<*>>>() {
        @IgnoredOnParcel
        @Transient
        override val findRemote: RemoteIdModel? = value.firstNotNullOfOrNull { it.findRemote }
        @IgnoredOnParcel
        @Transient
        override val findLocals: Set<LocalIdModel> = value.flatMap { it.findLocals }.toSet()
        @IgnoredOnParcel
        @Transient
        override val preferRemote: MediaIdModel<*> = findRemote ?: value.first()
        @IgnoredOnParcel
        @Transient
        override val preferLocal: MediaIdModel<*> = findLocals.firstOrNull() ?: value.first()
        @IgnoredOnParcel
        @Transient
        override val serializableId: String = "group:${value.joinToString(",") { it.serializableId }}"

        @IgnoredOnParcel
        override val syncState: MediaItemSyncStateModel = when {
            value.any { it is DownloadingIdModel } -> DOWNLOADING
            value.any { it is UploadingIdModel } -> UPLOADING
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
                value: Collection<MediaIdModel<*>>,
                isVideo: Boolean,
            ) =
                GroupIdModel(ArrayList(value.distinct()), isVideo)
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