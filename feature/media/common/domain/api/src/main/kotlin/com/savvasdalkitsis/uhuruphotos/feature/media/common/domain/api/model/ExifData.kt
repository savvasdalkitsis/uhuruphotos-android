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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model

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
private fun Float.round(decimals: Int = 2): String = "%.${decimals}f".format(this)