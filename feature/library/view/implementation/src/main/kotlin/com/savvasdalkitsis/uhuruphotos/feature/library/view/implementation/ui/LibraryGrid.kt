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
package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.R
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.AutoAlbumsSelected
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.FavouritePhotosSelected
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.HiddenPhotosSelected
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.ItemOrderChanged
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.LibraryAction
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.TrashSelected
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.UserAlbumsSelected
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.AUTO
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.FAVOURITE
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.HIDDEN
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.LOCAL
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.TRASH
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.USER
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryLocalMediaState.FoundState
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryLocalMediaState.RequiresPermissionsState
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.NamedVitrine
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher
import kotlinx.collections.immutable.toImmutableList
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyGridState
import sh.calvin.reorderable.rememberReorderableLazyGridState

@Composable
internal fun LibraryGrid(
    contentPadding: PaddingValues,
    state: LibraryState,
    action: (LibraryAction) -> Unit
) {
    if (state.items.isEmpty())
        return
    val permissionLauncher = rememberPermissionFlowRequestLauncher()
    val data = remember { mutableStateOf(state.items) }
    val lazyGridState = rememberLazyGridState()
    val reordering = rememberReorderableLazyGridState(
        lazyGridState = lazyGridState,
        onMove = { from, to ->
            data.value = data.value.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }.toImmutableList()
            action(ItemOrderChanged(data.value))
        }
    )

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = contentPadding,
        columns = GridCells.Adaptive(160.dp),
        state = lazyGridState,
    ) {
        for (item in data.value) {
            when (item) {
                TRASH -> if (state.showTrash) {
                    pillItem(reordering, item, drawable.ic_delete, { GridItemSpan(maxCurrentLineSpan / 2) }) {
                        action(TrashSelected)
                    }
                }
                HIDDEN -> if (state.showHidden){
                    pillItem(reordering, item, drawable.ic_invisible, { GridItemSpan(maxCurrentLineSpan) }) {
                        action(HiddenPhotosSelected)
                    }
                }
                LOCAL -> item(item.title, { GridItemSpan(maxLineSpan) }) {
                    ReorderableItem(reordering, item.title) { isDragging ->
                        Vibrate(isDragging)
                        val title = stringResource(item.title)
                        when (val media = state.localMedia) {
                            is FoundState -> LocalFolders(
                                modifier = Modifier.draggableHandle(),
                                title = title,
                                media = media,
                                action = action
                            )
                            is RequiresPermissionsState -> LibraryPillItem(
                                modifier = Modifier.draggableHandle(),
                                title = title,
                                icon = drawable.ic_folder,
                            ) {
                                permissionLauncher.launch(media.deniedPermissions.toTypedArray<String>())
                            }
                            null -> {}
                        }
                    }
                }
                AUTO -> libraryItem(reordering, state.autoAlbums, item, R.drawable.ic_album_auto) {
                    action(AutoAlbumsSelected)
                }
                USER -> libraryItem(reordering, state.userAlbums, item, R.drawable.ic_album_user) {
                    action(UserAlbumsSelected)
                }
                FAVOURITE -> libraryItem(reordering, state.favouritePhotos, item, R.drawable.ic_album_favourites) {
                    action(FavouritePhotosSelected)
                }
            }
        }
    }
}

internal fun LazyGridScope.pillItem(
    reorder: ReorderableLazyGridState,
    item: LibraryItemState,
    @DrawableRes icon: Int,
    span: (LazyGridItemSpanScope.() -> GridItemSpan)? = null,
    onSelected: () -> Unit,
) {
    item(item.title, span) {
        val title = stringResource(item.title)
        ReorderableItem(reorder, item.title) { isDragging ->
            Vibrate(isDragging)
            LibraryPillItem(
                modifier = Modifier.draggableHandle(),
                title = title,
                icon = icon,
                onSelected = onSelected
            )
        }
    }
}

internal fun LazyGridScope.libraryItem(
    reordering: ReorderableLazyGridState,
    vitrineState: VitrineState?,
    item: LibraryItemState,
    iconFallback: Int,
    onSelected: () -> Unit,
) {
    vitrineState?.let {
        item(item.title) {
            val title = stringResource(item.title)
            ReorderableItem(reordering, item.title) { isDragging ->
                Vibrate(isDragging)
                NamedVitrine(
                    state = vitrineState,
                    photoGridModifier = Modifier.fillMaxWidth(),
                    iconFallback = iconFallback,
                    title = title,
                    onSelected = onSelected
                )
            }
        }
    }
}

@Composable
private fun Vibrate(isDragging: Boolean) {
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(isDragging) {
        if (isDragging) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
}