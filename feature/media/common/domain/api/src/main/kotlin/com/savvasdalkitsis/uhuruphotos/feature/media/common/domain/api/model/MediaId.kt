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
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.DOWNLOADING
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.LOCAL_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.PROCESSING
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.REMOTE_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.SYNCED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.UPLOADING
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@kotlinx.serialization.Serializable
@Immutable
sealed class MediaId<T : Serializable> private constructor(
) : Parcelable {

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

    val isBothRemoteAndLocal: Boolean get() = findLocals.isNotEmpty() && findRemote != null

    @Parcelize
    @kotlinx.serialization.Serializable
    data class Remote(
        override val value: String,
        override val isVideo: Boolean,
    ): MediaId<String>() {
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val preferRemote = this
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val preferLocal = this
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val findRemote = this
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val findLocals: Set<Local> = emptySet()

        @IgnoredOnParcel
        override val syncState: MediaItemSyncState = REMOTE_ONLY
        override fun fullResUri(serverUrl: String?): String =
            value.toFullSizeUrlFromId(isVideo, serverUrl!!)
        override fun thumbnailUri(serverUrl: String?): String =
            value.toThumbnailUrlFromId(serverUrl!!)

        fun toDownloading() = Downloading(value, isVideo)
    }

    @Parcelize
    @kotlinx.serialization.Serializable
    data class Downloading(
        override val value: String,
        override val isVideo: Boolean,
    ): MediaId<String>() {
        @IgnoredOnParcel
        val remote get() = Remote(value, isVideo)

        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val preferRemote = remote
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val preferLocal = this
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val findRemote = remote
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val findLocals: Set<Local> = emptySet()

        @IgnoredOnParcel
        override val syncState: MediaItemSyncState = DOWNLOADING
        override fun fullResUri(serverUrl: String?): String = remote.fullResUri(serverUrl)
        override fun thumbnailUri(serverUrl: String?): String = remote.thumbnailUri(serverUrl)
    }

    @Parcelize
    @kotlinx.serialization.Serializable
    data class Uploading(
        override val value: Long,
        val folderId: Int,
        override val isVideo: Boolean,
        val contentUri: String,
        val thumbnailUri: String,
    ): MediaId<Long>() {
        @IgnoredOnParcel
        val local get() = Local(value, folderId, isVideo, contentUri, thumbnailUri)

        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val preferRemote = this
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val preferLocal = local
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val findRemote = null
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val findLocals: Set<Local> = setOf(local)

        @IgnoredOnParcel
        @kotlinx.serialization.Transient
        override val syncState: MediaItemSyncState = UPLOADING
        override fun fullResUri(serverUrl: String?): String = local.fullResUri(serverUrl)
        override fun thumbnailUri(serverUrl: String?): String = thumbnailUri
    }


    @Parcelize
    @kotlinx.serialization.Serializable
    data class Processing(
        override val value: Long,
        val folderId: Int,
        override val isVideo: Boolean,
        val contentUri: String,
        val thumbnailUri: String,
    ): MediaId<Long>() {
        @IgnoredOnParcel
        val local get() = Local(value, folderId, isVideo, contentUri, thumbnailUri)

        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val preferRemote = this
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val preferLocal = local
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val findRemote = null
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val findLocals: Set<Local> = setOf(local)

        @IgnoredOnParcel
        @kotlinx.serialization.Transient
        override val syncState: MediaItemSyncState = PROCESSING

        override fun fullResUri(serverUrl: String?): String = local.fullResUri(serverUrl)
        override fun thumbnailUri(serverUrl: String?): String = thumbnailUri
    }

    @Parcelize
    @kotlinx.serialization.Serializable
    data class Local(
        override val value: Long,
        val folderId: Int,
        override val isVideo: Boolean,
        val contentUri: String,
        val thumbnailUri: String,
    ): MediaId<Long>() {
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val preferRemote = this
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val preferLocal = this
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val findRemote = null
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val findLocals: Set<Local> = setOf(this)

        @IgnoredOnParcel
        @kotlinx.serialization.Transient
        override val syncState: MediaItemSyncState = LOCAL_ONLY
        override fun fullResUri(serverUrl: String?): String = contentUri
        override fun thumbnailUri(serverUrl: String?): String = thumbnailUri

        fun toUploading() = Uploading(value, folderId, isVideo, contentUri, thumbnailUri)
        fun toProcessing() = Processing(value, folderId, isVideo, contentUri, thumbnailUri)
    }

    @Suppress("DataClassPrivateConstructor")
    @Parcelize
    @kotlinx.serialization.Serializable
    data class Group private constructor(
        override val value: ArrayList<MediaId<*>>,
        override val isVideo: Boolean,
    ): MediaId<ArrayList<MediaId<*>>>() {
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val findRemote: Remote? = value.firstNotNullOfOrNull { it.findRemote }
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val findLocals: Set<Local> = value.flatMap { it.findLocals }.toSet()
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val preferRemote: MediaId<*> = findRemote ?: value.first()
        @IgnoredOnParcel
        @Transient
        @kotlinx.serialization.Transient
        override val preferLocal: MediaId<*> = findLocals.firstOrNull() ?: value.first()

        @IgnoredOnParcel
        override val syncState: MediaItemSyncState = when {
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