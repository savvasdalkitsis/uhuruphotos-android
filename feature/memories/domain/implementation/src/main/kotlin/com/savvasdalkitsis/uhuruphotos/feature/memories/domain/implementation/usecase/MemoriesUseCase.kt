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
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateParser
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.joda.time.DateTime
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
@ActivityRetainedScoped
class MemoriesUseCase @Inject constructor(
    private val feedUseCase: FeedUseCase,
    private val dateParser: DateParser,
) : MemoriesUseCase {

    override fun observeMemories(): Flow<List<MemoryCollection>> =
        feedUseCase.observeFeed()
            .distinctUntilChanged()
            .map {
                it.findMemories()
            }

    override suspend fun getMemories(): List<MemoryCollection> =
        feedUseCase.getFeed().findMemories()

    private fun DateTime?.sameAsNow(field: DateTime.() -> Int) =
        this != null && field(this) == field(DateTime.now())

    private val MediaCollection.dateTime get() = dateParser.parseDateOrTimeString(unformattedDate)

    private fun List<MediaCollection>.findMemories(): List<MemoryCollection> =
        filter {
            with(it.dateTime) {
                sameAsNow { dayOfMonth } && sameAsNow { monthOfYear } && !sameAsNow { year }
            }
        }.mapNotNull { mediaCollection ->
            mediaCollection.dateTime?.year?.let { memoryYear ->
                MemoryCollection(
                    yearsAgo = DateTime.now().year - memoryYear,
                    mediaCollection = mediaCollection,
                )
            }
        }
}
