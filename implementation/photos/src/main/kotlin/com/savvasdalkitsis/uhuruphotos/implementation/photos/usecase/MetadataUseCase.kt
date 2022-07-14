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

import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.TAG_APERTURE_VALUE
import androidx.exifinterface.media.ExifInterface.TAG_DIGITAL_ZOOM_RATIO
import androidx.exifinterface.media.ExifInterface.TAG_FOCAL_LENGTH
import androidx.exifinterface.media.ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM
import androidx.exifinterface.media.ExifInterface.TAG_F_NUMBER
import androidx.exifinterface.media.ExifInterface.TAG_IMAGE_LENGTH
import androidx.exifinterface.media.ExifInterface.TAG_IMAGE_WIDTH
import androidx.exifinterface.media.ExifInterface.TAG_ISO_SPEED
import androidx.exifinterface.media.ExifInterface.TAG_ISO_SPEED_RATINGS
import androidx.exifinterface.media.ExifInterface.TAG_MODEL
import androidx.exifinterface.media.ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY
import androidx.exifinterface.media.ExifInterface.TAG_PIXEL_X_DIMENSION
import androidx.exifinterface.media.ExifInterface.TAG_PIXEL_Y_DIMENSION
import androidx.exifinterface.media.ExifInterface.TAG_SHUTTER_SPEED_VALUE
import androidx.exifinterface.media.ExifInterface.TAG_SUBJECT_DISTANCE
import coil.disk.DiskCache
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.read
import javax.inject.Inject
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

class MetadataUseCase @Inject constructor(
    private val diskCache: DiskCache,
) {

    suspend fun extractMetadata(imageUrl: String): PhotoMetadata? = read {
        diskCache[imageUrl]?.let { snapshot ->
            val file = snapshot.data.toFile()
            with(ExifInterface(file)) {
                PhotoMetadata(
                    size = "${file.length().mb.round(2)} MB",
                    exifData = ExifData(
                        fStop = (
                                double(TAG_F_NUMBER) ?:
                                ratio(TAG_APERTURE_VALUE)?.let {
                                    exp(it * ln(2.0) * 0.5)
                                }
                                )?.let { "ƒ/${it.round(2)}" },
                        shutterSpeed = ratio(TAG_SHUTTER_SPEED_VALUE)?.let {
                            "1/${2.0.pow(it).round(2)}"
                        },
                        isoSpeed = int(TAG_PHOTOGRAPHIC_SENSITIVITY)?.let {
                            "ISO$it"
                        },
                        camera = getAttribute(TAG_MODEL),
                        focalLength = ratio(TAG_FOCAL_LENGTH)?.let {
                            "${it.round(2)}mm"
                        },
                        focalLength35Equivalent = int(TAG_FOCAL_LENGTH_IN_35MM_FILM)?.let {
                            "${it}mm (35mm equivalent)"
                        },
                        width = int(TAG_PIXEL_X_DIMENSION) ?: int(TAG_IMAGE_WIDTH),
                        height = int(TAG_PIXEL_Y_DIMENSION) ?: int(TAG_IMAGE_LENGTH),
                        subjectDistance = double(TAG_SUBJECT_DISTANCE)?.let {
                            "${it.round(2)}m"
                        },
                        digitalZoomRatio = double(TAG_DIGITAL_ZOOM_RATIO)?.round(2)
                    ),
                )
            }
        }
    }

    private fun ExifInterface.int(attribute: String) =
        getAttributeInt(attribute, -1).takeIf { it >= 0 }
    private fun ExifInterface.double(attribute: String) =
        getAttributeDouble(attribute, -1.0).takeIf { it >= 0 }
    private fun ExifInterface.ratio(attribute: String): Double? =
        getAttribute(attribute)?.let {
            val ratio = it.split("/")
            if (ratio.size != 2) {
                null
            } else {
                val numerator = ratio[0].toIntOrNull()
                val denominator = ratio[1].toIntOrNull()
                if (numerator != null && denominator != null) {
                    numerator / denominator.toDouble()
                } else {
                    null
                }
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