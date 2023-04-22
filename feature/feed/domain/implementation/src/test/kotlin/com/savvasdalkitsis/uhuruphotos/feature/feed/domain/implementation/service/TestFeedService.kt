/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.service

import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.collectionId
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollectionById
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollectionsByDate
import io.mockk.coEvery
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun FeedService.respondsForCollection(id: Int, remoteMediaCollection: RemoteMediaCollection.Complete) {
    coEvery { getRemoteMediaCollection(collectionId(id), 1) } returns RemoteMediaCollectionById(
        remoteMediaCollection
    )
}

fun FeedService.willRespondForCollection(id: Int, remoteMediaCollection: RemoteMediaCollection.Complete): Mutex {
    val response = Mutex(locked = true)
    coEvery { getRemoteMediaCollection(collectionId(id), 1) } coAnswers {
        response.withLock {
            RemoteMediaCollectionById(remoteMediaCollection)
        }
    }
    return response
}
fun FeedService.respondsWith(vararg albums: RemoteMediaCollection.Incomplete) {
    coEvery { getRemoteMediaCollectionsByDate() } returns RemoteMediaCollectionsByDate(listOf(*albums))
}
fun FeedService.willRespondWith(vararg albums: RemoteMediaCollection.Incomplete): Mutex {
    val response = Mutex(locked = true)
    coEvery { getRemoteMediaCollectionsByDate() } coAnswers {
        response.withLock {
            RemoteMediaCollectionsByDate(listOf(*albums))
        }
    }
    return response
}

fun Mutex.completes() = unlock()
