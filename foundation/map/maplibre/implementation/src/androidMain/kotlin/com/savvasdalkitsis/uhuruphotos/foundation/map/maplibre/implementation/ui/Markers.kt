/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.map.maplibre.implementation.ui

import android.graphics.Color
import androidx.annotation.ColorInt
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import dev.icerock.moko.resources.ImageResource

enum class Markers(
    val id: String,
    val drawable: ImageResource,
    @ColorInt
    val tint: Int,
) {
    Pin("pin", images.ic_pin, Color.RED)
}
