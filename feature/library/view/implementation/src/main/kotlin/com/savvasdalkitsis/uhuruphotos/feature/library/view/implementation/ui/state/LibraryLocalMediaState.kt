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
package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
sealed class LibraryLocalMediaState : Parcelable {

    @Immutable
    data class RequiresPermissionsState(val deniedPermissions: ImmutableList<String>) : LibraryLocalMediaState()

    @Immutable
    data class FoundState(
        val buckets: ImmutableList<Pair<LocalMediaFolder, VitrineState>>,
        val scanningOtherFolders: Boolean,
    ) : LibraryLocalMediaState()
}