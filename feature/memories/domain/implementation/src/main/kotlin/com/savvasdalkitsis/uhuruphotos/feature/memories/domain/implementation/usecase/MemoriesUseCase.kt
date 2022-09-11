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
package com.savvasdalkitsis.uhuruphotos.feature.memories.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.memories.domain.api.model.MemorySummary
import com.savvasdalkitsis.uhuruphotos.feature.memories.domain.api.usecase.MemoriesUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule.ParsingDateFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

class MemoriesUseCase @Inject constructor(
    private val feedUseCase: FeedUseCase,
    @ParsingDateFormat
    private val dateFormat: DateFormat,
) : MemoriesUseCase {

    override fun observeMemories(): Flow<List<MediaCollection>> =
        feedUseCase.observeFeed().map { feed ->
            val today = Calendar.getInstance()
            feed.filter { mediaCollection ->
                mediaCollection.calendar?.let { c ->
                    c.day == today.day && c.month == today.month && c.year != today.year
                } == true
            }
        }

    override fun observeMemorySummaries(): Flow<List<MemorySummary>> =
        observeMemories().map { mediaCollections ->
            val today = Calendar.getInstance()
            mediaCollections.mapNotNull { mediaCollection ->
                mediaCollection.calendar?.year?.let { mediaCollection to today.year - it }
            }.map { (mediaCollection, diff) ->
                MemorySummary(
                    yearsAgo = diff,
                    mediaItem = mediaCollection.mediaItems.find { it.isFavourite }
                        ?: mediaCollection.mediaItems.first()
                )
            }
        }

    private val MediaCollection.calendar get() = unformattedDate?.let {
        dateFormat.parse(it)
    }?.let {
        Calendar.getInstance().apply { time = it }
    }

    private val Calendar.day get() = get(Calendar.DAY_OF_MONTH)
    private val Calendar.month get() = get(Calendar.MONTH)
    private val Calendar.year get() = get(Calendar.YEAR)

}