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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state

import android.os.Parcelable
import androidx.compose.runtime.Composable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Parcelize
sealed class Title : Parcelable {
    @Composable
    abstract fun toText(): String

    data class Text(val title: String) : Title() {
        @Composable
        override fun toText(): String = title
    }

    data class Resource(val title: @RawValue StringResource) : Title() {
        @Composable
        override fun toText(): String = stringResource(title)
    }

}

fun String?.toTitleOr(title: StringResource): Title = this?.let { Title.Text(it) } ?: Title.Resource(
    title
)
