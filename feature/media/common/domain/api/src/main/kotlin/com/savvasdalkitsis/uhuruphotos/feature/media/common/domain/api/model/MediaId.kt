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
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.DOWNLOADING
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.LOCAL_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.REMOTE_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.SYNCED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.UPLOADING
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.Serializable

sealed class MediaId<T : Serializable> private constructor(
    @Transient
    open val value: T,
    @Transient
    open val isVideo: Boolean,
) : Parcelable {

    fun matches(id: MediaId<*>) =
        this == id || preferLocal == id.preferLocal || preferRemote == id.preferRemote

    abstract val thumbnailUri: String
    abstract val fullResUri: String
    abstract val syncState: MediaItemSyncState

    abstract val preferRemote: MediaId<*>
    abstract val findRemote: Remote?
    abstract val preferLocal: MediaId<*>
    abstract val findLocal: Local?

    val isBothRemoteAndLocal: Boolean get() = findLocal != null && findRemote != null

    @Parcelize
    data class Remote(
        override val value: String,
        override val isVideo: Boolean,
        val serverUrl: String,
    ): MediaId<String>(value, isVideo) {
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
        override val findLocal = null

        @IgnoredOnParcel
        override val syncState: MediaItemSyncState = REMOTE_ONLY
        @IgnoredOnParcel
        override val fullResUri = value.toFullSizeUrlFromId(isVideo, serverUrl)
        @IgnoredOnParcel
        override val thumbnailUri = value.toThumbnailUrlFromId(serverUrl)
    }

    @Parcelize
    data class Downloading(
        override val value: String,
        override val isVideo: Boolean,
        val serverUrl: String,
    ): MediaId<String>(value, isVideo) {
        @IgnoredOnParcel
        val remote get() = Remote(value, isVideo, serverUrl)

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
        override val findLocal = null

        @IgnoredOnParcel
        override val syncState: MediaItemSyncState = DOWNLOADING
        @IgnoredOnParcel
        override val fullResUri = remote.fullResUri
        @IgnoredOnParcel
        override val thumbnailUri = remote.thumbnailUri
    }

    @Parcelize
    data class Uploading(
        override val value: Long,
        override val isVideo: Boolean,
        val contentUri: String,
        override val thumbnailUri: String,
    ): MediaId<Long>(value, isVideo) {
        @IgnoredOnParcel
        val local get() = Local(value, isVideo, contentUri, thumbnailUri)

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
        override val findLocal = local

        @IgnoredOnParcel
        override val syncState: MediaItemSyncState = UPLOADING
        @IgnoredOnParcel
        override val fullResUri = local.fullResUri
    }

    @Parcelize
    data class Local(
        override val value: Long,
        override val isVideo: Boolean,
        val contentUri: String,
        override val thumbnailUri: String,
    ): MediaId<Long>(value, isVideo) {
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
        override val findLocal = this

        @IgnoredOnParcel
        override val syncState: MediaItemSyncState = LOCAL_ONLY
        @IgnoredOnParcel
        override val fullResUri = contentUri
    }

    @Suppress("DataClassPrivateConstructor")
    @Parcelize
    data class Group private constructor(
        override val value: ArrayList<MediaId<*>>,
        override val isVideo: Boolean,
    ): MediaId<ArrayList<MediaId<*>>>(value, isVideo) {
        @IgnoredOnParcel
        @Transient
        override val findRemote: Remote? = value.firstNotNullOfOrNull { it.findRemote }
        @IgnoredOnParcel
        @Transient
        override val findLocal: Local? = value.firstNotNullOfOrNull { it.findLocal }
        @IgnoredOnParcel
        @Transient
        override val preferRemote: MediaId<*> = findRemote ?: value.first()
        @IgnoredOnParcel
        @Transient
        override val preferLocal: MediaId<*> = findLocal ?: value.first()

        @IgnoredOnParcel
        override val syncState: MediaItemSyncState = when {
            value.any { it is Downloading } -> DOWNLOADING
            value.any { it is Uploading } -> UPLOADING
            findRemote == null -> LOCAL_ONLY
            findLocal == null -> REMOTE_ONLY
            else -> SYNCED
        }
        @IgnoredOnParcel
        override val fullResUri = preferLocal.fullResUri
        @IgnoredOnParcel
        override val thumbnailUri = if (isVideo)
                preferRemote.thumbnailUri
            else
                preferLocal.thumbnailUri

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