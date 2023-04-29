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
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.*
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.Serializable

sealed class MediaId<T : Serializable> private constructor(
    @Transient
    open val value: T,
    @Transient
    open val isVideo: Boolean,
) : Parcelable {

    abstract val preferRemote: MediaId<*>
    abstract val findRemote: Remote?
    abstract val preferLocal: MediaId<*>
    abstract val findLocal: Local?
    abstract val syncState: MediaItemSyncState

    val isBothRemoteAndLocal: Boolean get() = findLocal != null && findRemote != null

    @Parcelize
    data class Remote(
        override val value: String,
        override val isVideo: Boolean,
    ): MediaId<String>(value, isVideo) {
        @IgnoredOnParcel
        override val preferRemote = this
        @IgnoredOnParcel
        override val preferLocal = this
        @IgnoredOnParcel
        override val findRemote = this
        @IgnoredOnParcel
        override val findLocal = null
        @IgnoredOnParcel
        override val syncState: MediaItemSyncState = REMOTE_ONLY
    }

    @Parcelize
    data class Local(
        override val value: Long,
        override val isVideo: Boolean,
    ): MediaId<Long>(value, isVideo) {
        @IgnoredOnParcel
        override val preferRemote = this
        @IgnoredOnParcel
        override val preferLocal = this
        @IgnoredOnParcel
        override val findRemote = null
        @IgnoredOnParcel
        override val findLocal = this
        @IgnoredOnParcel
        override val syncState: MediaItemSyncState = LOCAL_ONLY
    }

    @Suppress("DataClassPrivateConstructor")
    @Parcelize
    data class Group private constructor(
        override val value: ArrayList<MediaId<*>>,
        override val isVideo: Boolean,
    ): MediaId<ArrayList<MediaId<*>>>(value, isVideo) {
        @IgnoredOnParcel
        override val findRemote: Remote? = value.firstNotNullOfOrNull { it as? Remote }
        @IgnoredOnParcel
        override val findLocal: Local? = value.firstNotNullOfOrNull { it as? Local }
        @IgnoredOnParcel
        override val preferRemote: MediaId<*> = findRemote ?: value.first()
        @IgnoredOnParcel
        override val preferLocal: MediaId<*> = findLocal ?: value.first()
        @IgnoredOnParcel
        override val syncState: MediaItemSyncState = when {
            findRemote == null -> LOCAL_ONLY
            findLocal == null -> REMOTE_ONLY
            else -> SYNCED
        }

        companion object {
            operator fun invoke(
                value: Collection<MediaId<*>>,
                isVideo: Boolean,
            ) =
                Group(ArrayList(value.distinct()), isVideo)
        }
    }
}
