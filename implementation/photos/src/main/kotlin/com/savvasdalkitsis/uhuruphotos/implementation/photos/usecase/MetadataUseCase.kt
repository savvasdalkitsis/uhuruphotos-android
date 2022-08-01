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
package com.savvasdalkitsis.uhuruphotos.implementation.photos.usecase

import coil.disk.DiskCache
import com.savvasdalkitsis.api.exif.usecase.ExifUseCase
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.read
import javax.inject.Inject
import kotlin.math.pow

class MetadataUseCase @Inject constructor(
    private val diskCache: DiskCache,
    private val exifUseCase: ExifUseCase,
) {

    suspend fun extractMetadata(imageUrl: String): PhotoMetadata? = read {
        diskCache[imageUrl]?.let { snapshot ->
            val file = snapshot.data.toFile()
            val exif = exifUseCase.extractFrom(file)
            PhotoMetadata(
                size = "${file.length().mb.round(2)} MB",
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
}

data class PhotoMetadata(
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
    val megapixels: String? get() = if (width != null && height != null) {
        "${(width * height.toLong()).mb.round(2)} MP"
    } else {
        null
    }
    val wh: Pair<Int, Int>? get() = if (width != null && height != null) {
        width to height
    } else {
        null
    }
}

private val Long.mb get() = this / 1024 / 1024f
private fun Double.round(decimals: Int = 2): String = "%.${decimals}f".format(this)
private fun Float.round(decimals: Int = 2): String = "%.${decimals}f".format(this)