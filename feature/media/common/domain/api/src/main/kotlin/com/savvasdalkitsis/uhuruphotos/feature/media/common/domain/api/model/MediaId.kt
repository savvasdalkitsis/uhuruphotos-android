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

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.*

sealed class MediaId<T> private constructor(open val value: T) {

    abstract val preferRemote: MediaId<*>
    abstract val findRemote: Remote?
    abstract val preferLocal: MediaId<*>
    abstract val findLocal: Local?
    abstract val serialize: String
    abstract val syncState: MediaItemSyncState

    data class Remote(override val value: String): MediaId<String>(value) {
        override val preferRemote = this
        override val preferLocal = this
        override val findRemote = this
        override val findLocal = null
        override val serialize = "remote:$value"
        override val syncState: MediaItemSyncState = REMOTE_ONLY
    }

    data class Local(override val value: Long): MediaId<Long>(value) {
        override val preferRemote = this
        override val preferLocal = this
        override val findRemote = null
        override val findLocal = this
        override val serialize = "local:$value"
        override val syncState: MediaItemSyncState = LOCAL_ONLY
    }

    data class Group(override val value: Set<MediaId<*>>): MediaId<Set<MediaId<*>>>(value) {
        override val findRemote: Remote? = value.firstNotNullOfOrNull { it as? Remote }
        override val findLocal: Local? = value.firstNotNullOfOrNull { it as? Local }
        override val preferRemote: MediaId<*> = findRemote ?: value.first()
        override val preferLocal: MediaId<*> = findLocal ?: value.first()
        override val serialize = "group:" + value.joinToString(separator = "::") { it.serialize }
        override val syncState: MediaItemSyncState = when {
            findRemote == null -> LOCAL_ONLY
            findLocal == null -> REMOTE_ONLY
            else -> SYNCED
        }
    }

    companion object {
        operator fun invoke(id: String) = deserialize(id)

        private fun deserialize(id: String): MediaId<*> = when {
            id.startsWith("local:") -> Local(id.removePrefix("local:").toLong())
            id.startsWith("remote:") -> Remote(id.removePrefix("remote:"))
            else -> Group(id.removePrefix("group:").split("::").map {
                deserialize(it)
            }.toSet())
        }
    }
}
