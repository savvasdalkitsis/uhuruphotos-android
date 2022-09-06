package com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.service

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollectionsByDate
import io.mockk.coEvery
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


fun PersonService.willRespondForPersonWith(personId: Int, vararg albums: RemoteMediaCollection.Incomplete): Mutex {
    val response = Mutex(locked = true)
    coEvery { getMediaCollectionsForPerson(personId) } coAnswers {
        response.withLock {
            RemoteMediaCollectionsByDate(listOf(*albums))
        }
    }
    return response
}

// Commenting these out until the android gradle plugin supports kotlin test
// fixtures so I can reuse the TestRemoteMediaCollections methods here

fun PersonService.willRespondForPersonAlbum(personId: Int, albumId: Int, remoteMediaCollection: RemoteMediaCollection.Complete): Mutex {
    val response = Mutex(locked = true)
//    coEvery { getMediaCollectionForPerson(collectionId(albumId), personId) } coAnswers {
//        response.withLock {
//            RemoteMediaCollectionById(remoteMediaCollection)
//        }
//    }
    return response
}
