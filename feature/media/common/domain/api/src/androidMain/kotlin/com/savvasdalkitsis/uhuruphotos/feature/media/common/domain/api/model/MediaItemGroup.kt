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

data class MediaItemGroup(
    val remoteInstance: MediaItem? = null,
    val localInstances: Set<MediaItem> = emptySet(),
) : MediaItem {

    private val all = listOfNotNull(remoteInstance) + localInstances
    private val any = all.first()
    init {
        if (all.isEmpty()) {
            throw IllegalArgumentException("Media item group must contain at least one instance")
        }
        if (all.map { it.mediaHash }.toSet().size != 1) {
            throw IllegalArgumentException("Media item group must contain instances with the same media hash. $this")
        }
        if (all.map { it.id.isVideo }.toSet().size != 1) {
            throw IllegalArgumentException("Media item group must contain instances with the same media type (video/photo). $this")
        }
    }

    override val id: MediaId<*> = MediaId.Group(all.map { it.id }, any.id.isVideo)
    override val mediaHash = any.mediaHash
    override val fallbackColor: String? = all.prop { fallbackColor }
    override val displayDayDate: String? = all.prop { displayDayDate }
    override val sortableDate: String? = all.prop { sortableDate }
    override val isFavourite: Boolean = all.any { it.isFavourite }
    override val ratio: Float = all.firstOrNull { it.ratio != 1f }?.ratio ?: 1f
    override val latLng: (Pair<Double, Double>)? = all.prop { latLng }
    override val mediaDay: MediaDay? = all.prop { mediaDay }

    private fun <T> List<MediaItem>.prop(instance: MediaItem.() -> T): T? =
        firstOrNull { instance(it) != null }?.let(instance)
}