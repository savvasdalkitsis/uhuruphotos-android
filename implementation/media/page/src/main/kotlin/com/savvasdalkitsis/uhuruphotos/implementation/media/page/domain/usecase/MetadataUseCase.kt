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
package com.savvasdalkitsis.uhuruphotos.implementation.media.page.domain.usecase

import android.content.ContentResolver
import android.net.Uri
import coil.disk.DiskCache
import com.savvasdalkitsis.uhuruphotos.foundation.exif.api.model.ExifMetadata
import com.savvasdalkitsis.uhuruphotos.foundation.exif.api.usecase.ExifUseCase
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.read
import javax.inject.Inject
import kotlin.math.pow

class MetadataUseCase @Inject constructor(
    private val diskCache: DiskCache,
    private val exifUseCase: ExifUseCase,
    private val contentResolver: ContentResolver,
) {

    suspend fun extractMetadata(url: String): MediaItemMetadata? = read {
        when {
            url.startsWith("content://") -> fromUri(url)
            else -> fromUrl(url)
        }?.let { (size, exif) ->
            MediaItemMetadata(
                size = "${size.mb.round(2)} MB",
                exifData = ExifData(
                    fStop = exif.fStop?.let { "ƒ/${it.round(2)}" },
                    shutterSpeed = exif.shutterSpeed?.let {
                        "1/${2.0.pow(it).round(2)}"
                    },
                    isoSpeed = exif.isoSpeed?.let {
                        "ISO$it"
                    },
                    camera = exif.camera,
                    focalLength = exif.focalLength?.let {
                        "${it.round(2)}mm"
                    },
                    focalLength35Equivalent = exif.focalLength35Equivalent?.let {
                        "${it}mm (35mm equivalent)"
                    },
                    width = exif.width,
                    height = exif.height,
                    subjectDistance = exif.subjectDistance?.let {
                        "${it.round(2)}m"
                    },
                    digitalZoomRatio = exif.digitalZoomRatio?.round(2)
                ),
            )
        }
    }

    private fun fromUrl(url: String): Pair<Long, ExifMetadata>? {
        return diskCache[url]?.let { snapshot ->
            val file = snapshot.data.toFile()
            val exif = exifUseCase.extractFrom(file)
            file.length() to exif
        }
    }

    private fun fromUri(url: String): Pair<Long, ExifMetadata> {
        val uri = url.toUri
        val exif = uri.inputStream.use {
            exifUseCase.extractFrom(it)
        }
        val size = uri.inputStream.use {
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var totalRead = 0L
            do {
                val read = it.read(buffer)
                if (read > 0) {
                    totalRead += read
                }
            } while (read > 0)
            totalRead
        }
        return size to exif
    }

    private val String.toUri get() = Uri.parse(this)
    private val Uri.inputStream get() = contentResolver.openInputStream(this)!!
}

data class MediaItemMetadata(
    val size: String,
    val exifData: ExifData
)

data class ExifData(
    val fStop: String? = null,
    val shutterSpeed: String? = null,
    val isoSpeed: String? = null,
    val camera: String? = null,
    val focalLength: String? = null,
    val focalLength35Equivalent: String? = null,
    val subjectDistance: String? = null,
    val digitalZoomRatio: String? = null,
    val width: Int? = null,
    val height: Int? = null,
) {
    val megapixels: String? get() = whSafe { w, h ->
        "${(w * h.toLong()).mb.round(2)} MP"
    }
    val wh: Pair<Int, Int>? get() = whSafe { w, h ->
        w to h
    }

    private fun <T> whSafe(action: (Int, Int) -> T) : T? =
        if (width != null && width != 0 && height != null && height != 0) {
            action(width, height)
        } else {
            null
        }
}

private val Long.mb get() = this / 1024 / 1024f
private fun Double.round(decimals: Int = 2): String = "%.${decimals}f".format(this)
private fun Float.round(decimals: Int = 2): String = "%.${decimals}f".format(this)