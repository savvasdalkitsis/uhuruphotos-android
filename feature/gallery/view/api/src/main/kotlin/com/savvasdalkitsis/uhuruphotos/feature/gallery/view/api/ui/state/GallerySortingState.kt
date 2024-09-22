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

import androidx.annotation.DrawableRes
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable

enum class GallerySortingState(@DrawableRes val icon: Int) {

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