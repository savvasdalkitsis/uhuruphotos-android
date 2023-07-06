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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state

import android.content.pm.ResolveInfo
import androidx.annotation.StringRes
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemMetadata
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.Person
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon

data class LightboxState(
    private val _currentIndex: Int = 0,
    val media: List<SingleMediaItemState> = emptyList(),
    val isLoading: Boolean = false,
    @StringRes val errorMessage: Int? = null,
    val showUI: Boolean = true,
    val showDeleteConfirmationDialog: Boolean = false,
    val showTrashingConfirmationDialog: Boolean = false,
    val showFullySyncedDeleteConfirmationDialog: Boolean = false,
    val showRestorationConfirmationDialog: Boolean = false,
    val infoSheetHidden: Boolean = true,
    val showRestoreButton: Boolean = false,
    val missingPermissions: List<String> = emptyList(),
) {
    val currentIndex: Int = _currentIndex.coerceAtMost(media.size - 1).coerceAtLeast(0)
    val currentMediaItem: SingleMediaItemState get() = media[currentIndex]

    override fun toString(): String =
        """LightboxState(currentIndex=$currentIndex,
            |_currentIndex=$_currentIndex,
            | mediaCount=${media.size},
            | currentMedia=${if (media.isEmpty()) "EMPTY" else currentMediaItem.toString()},
            | isLoading=$isLoading,
            | errorMessage=$errorMessage,
            | showUI=$showUI,
            | showDeleteConfirmationDialog=$showDeleteConfirmationDialog,
            | showTrashingConfirmationDialog=$showTrashingConfirmationDialog,
            | showFullySyncedDeleteConfirmationDialog=$showFullySyncedDeleteConfirmationDialog,
            | showRestorationConfirmationDialog=$showRestorationConfirmationDialog,
            | infoSheetHidden=$infoSheetHidden,
            | showRestoreButton=$showRestoreButton)""".trimMargin().replace("\n", "")

    fun copyWithIndex(index: Int) = copy(
        _currentIndex = index.coerceAtLeast(0)
    )
}

data class SingleMediaItemState(
    val id: MediaId<*>,
    val isFavourite: Boolean? = null,
    val dateAndTime: String = "",
    val location: String = "",
    val gps: LatLon? = null,
    val showFavouriteIcon: Boolean = false,
    val showDeleteButton: Boolean = true,
    val showShareIcon: Boolean = false,
    val showUseAsIcon: Boolean = false,
    val showEditIcon: Boolean = false,
    val peopleInMediaItem: List<Person> = emptyList(),
    val metadata: MediaItemMetadata? = null,
    val md5: String? = null,
    val remotePath: String? = null,
    val localPath: String? = null,
    val loadingDetails: Boolean = false,
    val mediaItemSyncState: MediaItemSyncState? = null,
    val showEditApps: List<ResolveInfo> = emptyList(),
)