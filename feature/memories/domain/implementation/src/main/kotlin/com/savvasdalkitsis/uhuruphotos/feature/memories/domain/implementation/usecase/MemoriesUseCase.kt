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
import com.savvasdalkitsis.uhuruphotos.feature.memories.domain.api.model.MemoryCollection
import com.savvasdalkitsis.uhuruphotos.feature.memories.domain.api.usecase.MemoriesUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule.ParsingDateFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.danlew.android.joda.DateUtils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import javax.inject.Inject

class MemoriesUseCase @Inject constructor(
    private val feedUseCase: FeedUseCase,
    @ParsingDateFormat
    private val dateFormat: DateTimeFormatter,
) : MemoriesUseCase {

    override fun observeMemories(): Flow<List<MemoryCollection>> =
        feedUseCase.observeFeed().map { feed ->
            val today = DateTime.now()
            feed.filter { mediaCollection ->
                mediaCollection.dateTime?.let { DateUtils.isToday(it) } == true
            }.mapNotNull { mediaCollection ->
                mediaCollection.dateTime?.year?.let { memoryYear ->
                    MemoryCollection(
                        yearsAgo = today.year - memoryYear,
                        mediaCollection = mediaCollection,
                    )
                }
            }
        }

    private val MediaCollection.dateTime get() = unformattedDate?.let {
        try {
            dateFormat.parseDateTime(it)
        } catch (e: Exception) {
            null
        }
    }

}