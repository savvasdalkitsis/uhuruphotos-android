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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model

data class LocalMediaItem(
    val id: Long,
    val displayName: String?,
    val displayDate: String,
    val displayDateTime: String,
    val dateTaken: String,
    val dateTimeTaken: String,
    val sortableDate: String,
    val bucket: LocalMediaFolder,
    val width: Int,
    val height: Int,
    val size: Int,
    val contentUri: String,
    val thumbnailPath: String?,
    val md5: Md5Hash,
    val video: Boolean,
    val duration: Int?,
    val latLon: Pair<Double, Double>?,
    val fallbackColor: String?,
    val path: String?,
    val orientation: MediaOrientation,
)