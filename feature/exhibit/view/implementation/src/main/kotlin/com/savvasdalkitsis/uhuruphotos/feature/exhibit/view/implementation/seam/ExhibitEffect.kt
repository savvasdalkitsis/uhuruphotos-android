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
package com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon

sealed class ExhibitEffect {
    data class LaunchMap(val gps: LatLon) : ExhibitEffect()
    data class CopyToClipboard(val content: String) : ExhibitEffect()
    object HideSystemBars : ExhibitEffect()
    object ShowSystemBars : ExhibitEffect()
    object NavigateBack : ExhibitEffect()
    object ErrorRefreshingPeople : ExhibitEffect()
    object DownloadingOriginal : ExhibitEffect()

    data class ShareMedia(val url: String) : ExhibitEffect()
    data class UseMediaAs(val url: String) : ExhibitEffect()
    data class NavigateToPerson(val id: Int) : ExhibitEffect()
}
