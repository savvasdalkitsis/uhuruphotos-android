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

data class MediaItemGroupModel(
    val remoteInstance: MediaItemModel? = null,
    val localInstances: Set<MediaItemModel> = emptySet(),
) : MediaItemModel {

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

    override val id: MediaIdModel<*> = MediaIdModel.GroupIdModel(all.map { it.id }, any.id.isVideo, any.mediaHash)
    override val mediaHash = any.mediaHash
    override val fallbackColor: String? = all.prop { fallbackColor }
    override val displayDayDate: String? = all.prop { displayDayDate }
    override val sortableDate: String? = all.prop { sortableDate }
    override val isFavourite: Boolean = all.any { it.isFavourite }
    override val ratio: Float = all.firstOrNull { it.ratio != 1f }?.ratio ?: 1f
    override val latLng: (Pair<Double, Double>)? = all.prop { latLng }
    override val mediaDay: MediaDayModel? = all.prop { mediaDay }

    private fun <T> List<MediaItemModel>.prop(instance: MediaItemModel.() -> T): T? =
        firstOrNull { instance(it) != null }?.let(instance)
}