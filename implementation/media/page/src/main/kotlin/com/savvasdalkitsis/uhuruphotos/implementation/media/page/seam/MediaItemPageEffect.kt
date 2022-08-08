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
package com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam

import com.savvasdalkitsis.uhuruphotos.api.map.model.LatLon

sealed class MediaItemPageEffect {
    data class LaunchMap(val gps: LatLon) : MediaItemPageEffect()
    data class CopyToClipboard(val content: String) : MediaItemPageEffect()
    object HideSystemBars : MediaItemPageEffect()
    object ShowSystemBars : MediaItemPageEffect()
    object NavigateBack : MediaItemPageEffect()
    object ErrorRefreshingPeople : MediaItemPageEffect()
    object DownloadingOriginal : MediaItemPageEffect()

    data class ShareMedia(val url: String) : MediaItemPageEffect()
    data class UseMediaAs(val url: String) : MediaItemPageEffect()
    data class NavigateToPerson(val id: Int) : MediaItemPageEffect()
}
