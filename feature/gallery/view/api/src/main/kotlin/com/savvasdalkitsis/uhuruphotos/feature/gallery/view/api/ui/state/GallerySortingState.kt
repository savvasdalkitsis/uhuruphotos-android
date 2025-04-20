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
package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state

import org.jetbrains.compose.resources.DrawableResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_sort_date_ascending
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_sort_date_descending

enum class GallerySortingState(val icon: DrawableResource) {

    DATE_DESC(drawable.ic_sort_date_descending),
    DATE_ASC(drawable.ic_sort_date_ascending);

    fun toggle() = when (this) {
        DATE_DESC -> DATE_ASC
        else -> DATE_DESC
    }

    companion object {
        val default = DATE_DESC
    }
}