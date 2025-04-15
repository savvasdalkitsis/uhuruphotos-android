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
package com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes

import org.jetbrains.compose.resources.StringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.contrast_high
import uhuruphotos_android.foundation.strings.api.generated.resources.contrast_medium
import uhuruphotos_android.foundation.strings.api.generated.resources.contrast_normal

enum class ThemeContrast(val label: StringResource) {
    NORMAL(string.contrast_normal),
    MEDIUM(string.contrast_medium),
    HIGH(string.contrast_high);

    companion object {
        val default = NORMAL
    }
}