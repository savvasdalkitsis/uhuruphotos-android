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
package com.savvasdalkitsis.uhuruphotos.foundation.exif.implementation.usecase

import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.TAG_APERTURE_VALUE
import androidx.exifinterface.media.ExifInterface.TAG_DIGITAL_ZOOM_RATIO
import androidx.exifinterface.media.ExifInterface.TAG_FOCAL_LENGTH
import androidx.exifinterface.media.ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM
import androidx.exifinterface.media.ExifInterface.TAG_F_NUMBER
import androidx.exifinterface.media.ExifInterface.TAG_IMAGE_LENGTH
import androidx.exifinterface.media.ExifInterface.TAG_IMAGE_WIDTH
import androidx.exifinterface.media.ExifInterface.TAG_MODEL
import androidx.exifinterface.media.ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY
import androidx.exifinterface.media.ExifInterface.TAG_PIXEL_X_DIMENSION
import androidx.exifinterface.media.ExifInterface.TAG_PIXEL_Y_DIMENSION
import androidx.exifinterface.media.ExifInterface.TAG_SHUTTER_SPEED_VALUE
import androidx.exifinterface.media.ExifInterface.TAG_SUBJECT_DISTANCE
import com.savvasdalkitsis.uhuruphotos.foundation.exif.api.model.ExifMetadata
import com.savvasdalkitsis.uhuruphotos.foundation.exif.api.usecase.ExifUseCase
import java.io.File
import java.io.InputStream
import javax.inject.Inject
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow

class ExifUseCase @Inject constructor(
) : ExifUseCase {

    override fun extractFrom(file: File) = ExifInterface(file).metadata()
    override fun extractFrom(stream: InputStream) = ExifInterface(stream).metadata()

    private fun ExifInterface.metadata() = ExifMetadata(
        fStop = double(TAG_F_NUMBER)
            ?: ratio(TAG_APERTURE_VALUE)?.let { exp(it * ln(2.0) * 0.5) },
        shutterSpeed = ratio(TAG_SHUTTER_SPEED_VALUE)?.let {
            2.0.pow(it)
        },
        isoSpeed = int(TAG_PHOTOGRAPHIC_SENSITIVITY),
        camera = string(TAG_MODEL),
        focalLength = ratio(TAG_FOCAL_LENGTH),
        focalLength35Equivalent = int(TAG_FOCAL_LENGTH_IN_35MM_FILM),
        width = int(TAG_PIXEL_X_DIMENSION) ?: int(TAG_IMAGE_WIDTH),
        height = int(TAG_PIXEL_Y_DIMENSION) ?: int(TAG_IMAGE_LENGTH),
        subjectDistance = double(TAG_SUBJECT_DISTANCE),
        digitalZoomRatio = double(TAG_DIGITAL_ZOOM_RATIO),
        latLon = latLong?.let { (lat, lon) -> lat to lon },
    )

    private fun ExifInterface.string(attribute: String) =
        getAttribute(attribute)
    private fun ExifInterface.int(attribute: String) =
        getAttributeInt(attribute, -1).takeIf { it >= 0 }
    private fun ExifInterface.double(attribute: String) =
        getAttributeDouble(attribute, -1.0).takeIf { it >= 0 }
    private fun ExifInterface.ratio(attribute: String): Double? =
        getAttribute(attribute)?.ratio
    private val String.ratio: Double?
        get() {
            val ratio = split("/")
            return if (ratio.size != 2) {
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

