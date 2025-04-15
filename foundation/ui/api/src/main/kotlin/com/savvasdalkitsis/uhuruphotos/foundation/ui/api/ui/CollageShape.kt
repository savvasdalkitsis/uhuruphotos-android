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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui

import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import org.jetbrains.compose.resources.StringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.rectangle
import uhuruphotos_android.foundation.strings.api.generated.resources.rounded_rectangle

enum class CollageShape(
    val icon: Int,
    val text: StringResource,
) {
    RECTANGLE(drawable.ic_rectangle, string.rectangle),
    ROUNDED_RECTANGLE(drawable.ic_rounded_rectangle, string.rounded_rectangle);

    companion object {
        val default = ROUNDED_RECTANGLE
    }
}