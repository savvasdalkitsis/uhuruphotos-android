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

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJob
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.model.Uploads

internal class FeedLibraryFixture(
    days: Int,
    remotePerDay: Int,
    syncedLocals: Int,
    localOnly: Int,
    queuedUploads: Int,
) {
    private val userId = 1

    val remoteDays: List<MediaCollectionModel> = (0 until days).map { day ->
        MediaCollectionModel(
            id = "collection$day",
            mediaItems = (0 until remotePerDay).map { remoteItem(day, day * remotePerDay + it) },
            displayTitle = dayTitle(day),
            unformattedDate = sortableDate(day),
        )
    }

    val localMedia: List<MediaItemModel> =
        (0 until syncedLocals).map { localItem(it.toLong(), it * 7 % (days * remotePerDay), remotePerDay) } +
            (0 until localOnly).map { localItem((syncedLocals + it).toLong(), days * remotePerDay + it, remotePerDay) }

    val uploads = Uploads((0 until queuedUploads).map {
        UploadJob(localItemId = (syncedLocals + it).toLong(), displayName = null, contentUri = "content://media/$it")
    })

    fun merger() = FeedMergerUseCase(remoteDays.asSequence(), localMedia, emptySet(), LocalMediaInFlight.from(uploads))

    private fun hash(index: Int) = MediaItemHashModel(Md5Hash("%032x".format(index.toLong() * 2654435761L)), userId)

    private fun dayTitle(day: Int) = "Day $day"

    private fun sortableDate(day: Int) = "%04d-%02d-%02dT12:00:00".format(2000 + day / 365, 1 + day / 28 % 12, 1 + day % 28)

    private fun remoteItem(day: Int, index: Int): MediaItemModel {
        val hash = hash(index)
        return MediaItemInstanceModel(
            id = MediaIdModel.RemoteIdModel(hash.hash, false, hash),
            mediaHash = hash,
            displayDayDate = dayTitle(day),
            sortableDate = sortableDate(day),
        )
    }

    private fun localItem(id: Long, index: Int, remotePerDay: Int): MediaItemModel {
        val hash = hash(index)
        val day = index / remotePerDay
        return MediaItemInstanceModel(
            id = MediaIdModel.LocalIdModel(id, 1, false, "content://media/external/images/media/$id", "file:///thumbs/$id.jpg", hash),
            mediaHash = hash,
            displayDayDate = dayTitle(day),
            sortableDate = sortableDate(day),
        )
    }
}

internal fun <T> allocatedBytes(block: () -> T): Pair<T, Long> = measure(allocatedBytesMethod, block)

internal fun <T> cpuNanos(block: () -> T): Pair<T, Long> = measure(cpuTimeMethod, block)

private fun <T> measure(counter: java.lang.reflect.Method, block: () -> T): Pair<T, Long> {
    val before = counter.invoke(threadMxBean) as Long
    val result = block()
    return result to counter.invoke(threadMxBean) as Long - before
}

private val threadMxBean = Class.forName("java.lang.management.ManagementFactory")
    .getMethod("getThreadMXBean").invoke(null)

private val allocatedBytesMethod = Class.forName("com.sun.management.ThreadMXBean")
    .getMethod("getCurrentThreadAllocatedBytes")

private val cpuTimeMethod = Class.forName("java.lang.management.ThreadMXBean")
    .getMethod("getCurrentThreadCpuTime")
