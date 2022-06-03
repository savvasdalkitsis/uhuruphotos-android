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
package com.savvasdalkitsis.uhuruphotos.api.photos.model

data class Photo(
    val id: String,
    val thumbnailUrl: String? = null,
    val fullResUrl: String? = null,
    val fallbackColor: String? = null,
    val isFavourite: Boolean = false,
    val ratio: Float = 1f,
    val isVideo: Boolean = false,
    val selectionMode: SelectionMode = SelectionMode.UNDEFINED,
    val latLng: (Pair<Double, Double>)? = null,
)