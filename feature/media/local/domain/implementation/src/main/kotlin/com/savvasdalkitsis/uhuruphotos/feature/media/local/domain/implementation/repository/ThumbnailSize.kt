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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository

import kotlin.math.roundToInt

private const val THUMBNAIL_WIDTH = 400
private const val TALLEST_THUMBNAIL_HEIGHT = THUMBNAIL_WIDTH * 20

internal data class ThumbnailSize(val width: Int, val height: Int)

internal fun thumbnailSizeOf(width: Int, height: Int): ThumbnailSize {
    val scale = minOf(1f, THUMBNAIL_WIDTH / width.toFloat(), TALLEST_THUMBNAIL_HEIGHT / height.toFloat())
    return ThumbnailSize(width.scaledBy(scale), height.scaledBy(scale))
}

internal fun thumbnailSampleSizeOf(width: Int, height: Int): Int {
    val target = thumbnailSizeOf(width, height)
    var sampleSize = 1
    while (width / (sampleSize * 2) >= target.width && height / (sampleSize * 2) >= target.height) {
        sampleSize *= 2
    }
    return sampleSize
}

private fun Int.scaledBy(scale: Float) = (this * scale).roundToInt().coerceAtLeast(1)
