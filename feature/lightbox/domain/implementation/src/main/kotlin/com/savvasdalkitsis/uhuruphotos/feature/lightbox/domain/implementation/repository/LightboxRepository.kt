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
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.lightbox.LightboxDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.domain.api.model.LightboxDetails
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.ExifData
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemMetadata
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.toMediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.deserializePaths
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.deserializePeopleNames
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.serializePaths
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onIO
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
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

    fun MediaId.Remote.observeDetails(): Flow<LightboxDetails> = merge(
        observeDetails(value) {
            remoteMediaUseCase.refreshDetailsNow(value)
        },
        remoteMediaUseCase.observeRemoteMediaItemDetails(value)
            .onEach { remoteDetails ->
                remoteDetails.toLightboxDetails().upsert(value)
            }
            .mapNotNull { null },
    )

    fun MediaId.Local.observeDetails(): Flow<LightboxDetails> = merge(
        observeDetails(value.toString()) {
            localMediaUseCase.refreshLocalMediaItem(value, isVideo)
        },
        combine(
            localMediaUseCase.observeLocalMediaItem(value),
            userUseCase.observeUser(),
        ) { item, user ->
            item.toLightboxDetails(user?.id).upsert(value.toString())
            null
        }.filterNotNull()
    )

    private fun DbRemoteMediaItemDetails.toLightboxDetails(): DbLightboxDetails =
        dbLightboxDetails(imageHash).copy(
            formattedDatetime = dateDisplayer.dateTimeString(timestamp),
            location = location,
            lat = gpsLat?.toDoubleOrNull(),
            lon = gpsLon?.toDoubleOrNull(),
            remotePaths = imagePath,
            hash = imageHash,
            peopleInMediaItem = peopleNames,
            searchCaptions = captions,
        )

    private fun observeDetails(
        id: String,
        refresh: suspend () -> Unit,
    ): Flow<LightboxDetails> = lightboxDetailsQueries.get(id)
        .asFlow().mapToOneOrNull(Dispatchers.IO).distinctUntilChanged().map {
            it?.toLightboxDetails()
        }
        .onEach {
            if (it?.formattedDateTime == null) {
                onIO {
                    refresh()
                }
            }
        }
        .filterNotNull()

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
            hash = hash?.let { MediaItemHash(it) },
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

    private suspend fun DbLightboxDetails.upsert(id: String) {
        lightboxDetailsQueries.insert(this.mergeWith(getExisting(id)))
    }

    private suspend fun getExisting(id: String) =
        lightboxDetailsQueries.get(id).awaitSingleOrNull()

    private fun DbLightboxDetails.mergeWith(other: DbLightboxDetails?) = DbLightboxDetails(
        id = id,
        formattedDatetime = formattedDatetime ?: other?.formattedDatetime,
        location = location.orEmpty().ifBlank { other?.location },
        lat = lat ?: other?.lat,
        lon = lon ?: other?.lon,
        remotePaths = remotePaths joinPathsWith other?.remotePaths,
        localPaths = localPaths joinPathsWith other?.localPaths,
        hash = hash ?: other?.hash,
        peopleInMediaItem = peopleInMediaItem.orEmpty().ifBlank { other?.peopleInMediaItem },
        searchCaptions = searchCaptions ?: other?.searchCaptions,
        size = size ?: other?.size,
        fStop = fStop ?: other?.fStop,
        shutterSpeed = shutterSpeed ?: other?.shutterSpeed,
        isoSpeed = isoSpeed ?: other?.isoSpeed,
        camera = camera ?: other?.camera,
        focalLength = focalLength ?: other?.focalLength,
        focalLength35Equivalent = focalLength35Equivalent ?: other?.focalLength35Equivalent,
        subjectDistance = subjectDistance ?: other?.subjectDistance,
        digitalZoomRatio = digitalZoomRatio ?: other?.digitalZoomRatio,
        width = width ?: other?.width,
        height = height ?: other?.height,
    )

    private infix fun String?.joinPathsWith(other: String?): String =
        (this.deserializePaths + other.deserializePaths).serializePaths

    private fun dbLightboxDetails(id: String) = DbLightboxDetails(
        id = id,
        formattedDatetime = null,
        location = null,
        lat = null,
        lon = null,
        remotePaths = null,
        localPaths = null,
        hash = null,
        peopleInMediaItem = null,
        searchCaptions = null,
        size = null,
        fStop = null,
        shutterSpeed = null,
        isoSpeed = null,
        camera = null,
        focalLength = null,
        focalLength35Equivalent = null,
        subjectDistance = null,
        digitalZoomRatio = null,
        width = null,
        height = null,
    )

    private fun LocalMediaItem.toLightboxDetails(userId: Int?) =
        dbLightboxDetails(id.toString()).copy(
            formattedDatetime = displayDateTime,
            lat = latLon?.first,
            lon = latLon?.second,
            hash = md5.toMediaItemHash(userId).hash,
            localPaths = path ?: contentUri,
    )

    suspend fun saveMetadata(id: String, metadata: MediaItemMetadata) {
        metadata.toLightboxDetails(id).upsert(id)
    }

    private fun MediaItemMetadata.toLightboxDetails(id: String) = dbLightboxDetails(id).copy(
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
    )
}
