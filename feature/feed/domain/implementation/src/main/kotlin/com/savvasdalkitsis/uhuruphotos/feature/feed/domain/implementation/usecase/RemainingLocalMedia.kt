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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemModel

internal class RemainingLocalMedia(private val media: List<MediaItemModel>) {

    private data class ContentKey(val hash: MediaItemHashModel, val isVideo: Boolean)

    private val taken = BooleanArray(media.size)
    private val byContent = media.indices.groupBy { media[it].contentKey }
    private val byDay = media.indices.groupBy { media[it].displayDayDate }

    private val MediaItemModel.contentKey get() = ContentKey(mediaHash, id.isVideo)

    fun takeSameContentAs(item: MediaItemModel): Set<MediaItemModel> = take(byContent[item.contentKey])

    fun takeFromDay(day: String): Set<MediaItemModel> = take(byDay[day])

    fun remaining(): List<MediaItemModel> = media.filterIndexed { index, _ -> !taken[index] }

    private fun take(positions: List<Int>?): Set<MediaItemModel> {
        val available = positions.orEmpty().filterNot { taken[it] }
        available.forEach { taken[it] = true }
        return available.map { media[it] }.toSet()
    }
}
