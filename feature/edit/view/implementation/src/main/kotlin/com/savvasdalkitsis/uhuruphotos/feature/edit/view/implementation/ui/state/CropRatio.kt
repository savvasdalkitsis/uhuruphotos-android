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
package com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui.state

import androidx.annotation.DrawableRes
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable

enum class CropRatio(
    val ratio: Float,
    @DrawableRes val icon: Int,
    val label: String,
    val rotation: Float = 0f,
) {
    Ratio_16x9(16/9f, drawable.ic_crop_16_9, "16:9", 90f),
    Ratio_9x16(9/16f, drawable.ic_crop_16_9, "9:16"),
    Ratio_3x2(3/2f, drawable.ic_crop_3_2, "3:2", 90f),
    Ratio_2x3(2/3f, drawable.ic_crop_3_2, "2:3"),
    Ratio_Square(1f, drawable.ic_crop_square, "1:1"),
}