/*
Copyright 2026 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.module.LocalMediaModule
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule.ParsingDateFormat
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule.ParsingDateTimeFormat
import org.joda.time.format.DateTimeFormatter
import javax.inject.Inject

class LocalMediaItemMapper @Inject constructor(
    @LocalMediaModule.LocalMediaDateTimeFormat
    private val localMediaDateTimeFormat: DateTimeFormatter,
    @ParsingDateTimeFormat
    private val parsingDateTimeFormat: DateTimeFormatter,
    @ParsingDateFormat
    private val parsingDateFormat: DateTimeFormatter,
    private val dateDisplayer: DateDisplayer,
) {

    suspend fun toItems(details: List<LocalMediaItemDetails>) = details.map { toItem(it) }

    suspend fun toFolders(details: List<LocalMediaItemDetails>): Map<LocalMediaFolder, List<LocalMediaItem>> =
        toItems(details).groupBy { it.bucket.id }.values.associate { items ->
            val folder = items.first().bucket
            folder to items.map { it.copy(bucket = folder) }
        }

    suspend fun toItem(details: LocalMediaItemDetails): LocalMediaItem = with(details) {
        val date = localMediaDateTimeFormat.parseDateTime(dateTaken)
        val dateTimeString = parsingDateTimeFormat.print(date)
        LocalMediaItem(
            id = id,
            displayName = displayName,
            displayDate = dateDisplayer.dateString(dateTimeString),
            displayDateTime = dateDisplayer.dateTimeString(dateTimeString),
            dateTimeTaken = parsingDateTimeFormat.print(date),
            dateTaken = parsingDateFormat.print(date),
            sortableDate = parsingDateTimeFormat.print(date),
            bucket = LocalMediaFolder(id = bucketId, bucketName),
            width = width,
            height = height,
            size = size,
            contentUri = contentUri,
            thumbnailPath = thumbnailPath,
            md5 = Md5Hash(md5),
            video = video,
            duration = duration,
            latLon = latLon?.let(::parseLatLon),
            fallbackColor = fallbackColor,
            path = path,
            orientation = orientation(orientation),
        )
    }

    private fun parseLatLon(latLon: String): Pair<Double, Double>? {
        val value = latLon.split(",").map { it.toDoubleOrNull() }
        val lat = value.getOrNull(0)
        val lon = value.getOrNull(1)
        return if (value.size == 2 && lat != null && lon != null) lat to lon else null
    }

    private fun orientation(orientation: String?) = when (orientation) {
        "0" -> MediaOrientation.ORIENTATION_0
        "90" -> MediaOrientation.ORIENTATION_90
        "180" -> MediaOrientation.ORIENTATION_180
        "270" -> MediaOrientation.ORIENTATION_270
        else -> MediaOrientation.ORIENTATION_UNKNOWN
    }
}
