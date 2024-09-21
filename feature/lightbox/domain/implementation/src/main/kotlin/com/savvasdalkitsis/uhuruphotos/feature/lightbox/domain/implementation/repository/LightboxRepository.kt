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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.domain.implementation.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.lightbox.LightboxDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.domain.api.model.LightboxDetails
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.ExifData
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemMetadata
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.deserializePaths
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.deserializePeopleNames
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

typealias DbLightboxDetails = com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.lightbox.LightboxDetails

class LightboxRepository @Inject constructor(
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val localMediaUseCase: LocalMediaUseCase,
    private val userUseCase: UserUseCase,
    private val peopleUseCase: PeopleUseCase,
    private val lightboxDetailsQueries: LightboxDetailsQueries,
    private val dateDisplayer: DateDisplayer,
) {

    fun observeDetails(
        md5Hash: Md5Hash,
    ): Flow<LightboxDetails> = userUseCase.observeUser().flatMapLatest {
        lightboxDetailsQueries.get(md5Hash.value)
            .asFlow().mapToOneOrNull(Dispatchers.IO).distinctUntilChanged().map {
                it?.toLightboxDetails()
            }
            .filterNotNull()
    }

    suspend fun refreshDetails(id: MediaId<*>, mediaHash: MediaItemHash): SimpleResult = coroutineBinding {
        if (getDbDetails(mediaHash.md5) == null) {
            lightboxDetailsQueries.touch(mediaHash.md5.value)
        }
        id.refreshedLocal().bind()?.let { local ->
            lightboxDetailsQueries.updateLocal(
                formattedDateTime = local.displayDateTime,
                lat = local.latLon?.first,
                lon = local.latLon?.second,
                hash = mediaHash.hash,
                localPaths = local.path ?: local.contentUri,
                md5 = mediaHash.md5.value,
            )
        }
        id.refreshedRemote().bind()?.let { remote ->
            lightboxDetailsQueries.updateRemote(
                formattedDateTime = dateDisplayer.dateTimeString(remote.timestamp),
                location = remote.location,
                remotePaths = remote.imagePath,
                hash = remote.imageHash,
                peopleInMediaItem = remote.peopleNames,
                searchCaptions = remote.captions,
                md5 = mediaHash.md5.value,
            )
        }
    }

    private suspend fun MediaId<*>.refreshedRemote(): Result<DbRemoteMediaItemDetails?, Throwable> = coroutineBinding {
        findRemote?.let { remote ->
            remoteMediaUseCase.refreshDetailsNow(remote.value).bind()
            remoteMediaUseCase.observeRemoteMediaItemDetails(remote.value).firstOrNull()
        }
    }

    private suspend fun MediaId<*>.refreshedLocal(): Result<LocalMediaItem?, Throwable> = coroutineBinding {
        findLocals.firstOrNull()?.let { local ->
            localMediaUseCase.refreshLocalMediaItem(local.value, local.isVideo).bind()
            localMediaUseCase.getLocalMediaItem(local.value)
        }
    }

    private suspend fun getDbDetails(md5Hash: Md5Hash): DbLightboxDetails? =
        lightboxDetailsQueries.get(md5Hash.value).awaitSingleOrNull()

    private suspend fun DbLightboxDetails.toLightboxDetails(): LightboxDetails {
        val people = peopleUseCase.getPeopleByName().ifEmpty {
            peopleUseCase.refreshPeople()
            peopleUseCase.getPeopleByName()
        }
        val peopleNamesList = peopleInMediaItem.orEmpty().deserializePeopleNames
        val peopleInMediaItem = people
            .filter { it.name in peopleNamesList }
        return LightboxDetails(
            formattedDateTime = formattedDatetime,
            location = location,
            latLon = lat?.let { lat -> lon?.let { lon -> LatLon(lat, lon) } },
            remotePaths = remotePaths?.deserializePaths ?: emptySet(),
            localPaths = localPaths?.deserializePaths ?: emptySet(),
            hash = hash,
            peopleInMediaItem = peopleInMediaItem,
            searchCaptions = searchCaptions?.split(",")?.toList().orEmpty()
                .map { it.trim() }
                .toSet(),
            size = size,
            exifData = ExifData(
                fStop = fStop,
                shutterSpeed = shutterSpeed,
                isoSpeed = isoSpeed,
                camera = camera,
                focalLength = focalLength,
                focalLength35Equivalent = focalLength35Equivalent,
                subjectDistance = subjectDistance,
                digitalZoomRatio = digitalZoomRatio,
                width = width,
                height = height,
            )
        )
    }

    fun saveMetadata(md5Hash: Md5Hash, metadata: MediaItemMetadata) = with(metadata) {
        lightboxDetailsQueries.updateMetadata(
            size = size,
            fStop = exifData.fStop,
            shutterSpeed = exifData.shutterSpeed,
            isoSpeed = exifData.isoSpeed,
            camera = exifData.camera,
            focalLength = exifData.focalLength,
            focalLength35Equivalent = exifData.focalLength35Equivalent,
            subjectDistance = exifData.subjectDistance,
            digitalZoomRatio = exifData.digitalZoomRatio,
            width = exifData.width,
            height = exifData.height,
            md5 = md5Hash.value,
        )
    }
}
