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