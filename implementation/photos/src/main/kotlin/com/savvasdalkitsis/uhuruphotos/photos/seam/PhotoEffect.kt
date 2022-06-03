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
package com.savvasdalkitsis.uhuruphotos.photos.seam

import com.savvasdalkitsis.uhuruphotos.map.model.LatLon

sealed class PhotoEffect {
    data class LaunchMap(val gps: LatLon) : PhotoEffect()
    data class CopyToClipboard(val content: String) : PhotoEffect()
    object HideSystemBars : PhotoEffect()
    object ShowSystemBars : PhotoEffect()
    object NavigateBack : PhotoEffect()
    object ErrorRefreshingPeople : PhotoEffect()

    data class SharePhoto(val url: String) : PhotoEffect()
    data class NavigateToPerson(val id: Int) : PhotoEffect()
}
