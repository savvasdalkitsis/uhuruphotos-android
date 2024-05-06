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
package com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.processing.domain.api.model.ProcessingItem
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.actions.DismissMessageDialog
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.actions.ForceReUploadSelectedItems
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.actions.ProcessingAction
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.actions.SelectedProcessingItem
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.seam.actions.TappedProcessingItem
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.ui.state.ProcessingState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.files
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.Thumbnail
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.CustomColors
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.Checkable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.OkDialog
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.DynamicIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UpNavButton
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun Processing(
    state: ProcessingState,
    action: (ProcessingAction) -> Unit,
) {
    val showForceReUpload = state.showForceReUpload
    CommonScaffold(
        title = { Text(text = stringResource(strings.processing_media_on_server)) },
        navigationIcon = { UpNavButton() },
        actionBarContent = {
            AnimatedVisibility(visible = showForceReUpload) {
                ActionIcon(
                    onClick = { action(ForceReUploadSelectedItems) },
                    icon = images.ic_cloud_upload_progress
                )
            }
        }
    ) { contentPadding ->
        when {
            state.isLoading -> FullLoading()
            state.items.isEmpty() -> FullLoading {
                DynamicIcon(icon = files.animation_empty_json)
            }
            else -> LazyColumn(
                modifier = Modifier.padding(contentPadding),
            ) {
                for (item in state.items) {
                    item(item.localItemId) {
                        Checkable(
                            id = item.localItemId,
                            selectedScale = 0.9f,
                            selectionMode = when {
                                item.selected -> SelectionMode.SELECTED
                                showForceReUpload -> SelectionMode.UNSELECTED
                                else -> SelectionMode.UNDEFINED
                            },
                            onClick = {
                                when {
                                    showForceReUpload -> {
                                        action(SelectedProcessingItem(item))
                                    }
                                    else -> if (item.hasError || item.hasResponse) {
                                        action(TappedProcessingItem(item))
                                    }
                                }
                            },
                            onLongClick = {
                                if (!showForceReUpload) {
                                    action(SelectedProcessingItem(item))
                                }
                            },
                        ) {
                            ProcessingItemRow(item)
                        }
                    }
                }
            }
        }
        state.itemMessageToDisplay?.let { (item, message) ->
            OkDialog(
                title = stringResource(
                    when {
                        item.hasError -> strings.last_error_processing
                        item.hasResponse -> strings.last_response_processing
                        else -> strings.processing
                    }
                ),
                onDismiss = { action(DismissMessageDialog) },
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ProcessingItemRow(item)
                    Text(text = message)
                }
            }
        }
    }
}

@Composable
fun ProcessingItemRow(
    item: ProcessingItem,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(64.dp),
        horizontalArrangement = spacedBy(4.dp),
    ) {
        Thumbnail(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f),
            url = item.contentUri,
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = spacedBy(4.dp),
        ) {
            Text(
                text = item.displayName ?: "",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.body2,
            )
            Row(
                modifier = Modifier
                    .height(4.dp),
                horizontalArrangement = spacedBy(2.dp),
            ) {
                ProgressLine(item.hasError)
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                DynamicIcon(icon = images.ic_cloud_in_progress)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(strings.processing),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.body2,
                )
            }
        }
    }
}

@Composable
private fun RowScope.ProgressLine(error: Boolean) {
    when {
        error -> Box(modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .background(
                CustomColors.syncError,
                RoundedCornerShape(2.dp)
            )
        )
        else -> LinearProgressIndicator(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            strokeCap = StrokeCap.Round,
            color = MaterialTheme.colors.primary,
        )
    }
}

@Preview
@Composable
private fun ProcessingPreview() {
    PreviewAppTheme {
        CompositionLocalProvider(
            LocalThumbnailImageLoader provides ImageLoader(LocalContext.current)
        ) {
            Processing(
                ProcessingState(
                    isLoading = false,
                    items = persistentListOf(
                        ProcessingItem(
                            localItemId = 1,
                            displayName = "PXL_20230801_103507882.jpg",
                            contentUri = "",
                            md5 = Md5Hash(""),
                        ),
                        ProcessingItem(
                            localItemId = 2,
                            displayName = "PXL_20230801_103507850.jpg",
                            contentUri = "",
                            md5 = Md5Hash(""),
                        ),
                        ProcessingItem(
                            localItemId = 3,
                            displayName = "PXL_20230801_103507810.mp4",
                            contentUri = "",
                            md5 = Md5Hash(""),
                        ),
                        ProcessingItem(
                            localItemId = 30,
                            displayName = "PXL_20230801_103507810.mp4",
                            contentUri = "",
                            md5 = Md5Hash(""),
                        ),
                        ProcessingItem(
                            localItemId = 4,
                            displayName = "PXL_20230801_103507810.mp4",
                            contentUri = "",
                            md5 = Md5Hash(""),
                        ),
                        ProcessingItem(
                            localItemId = 5,
                            displayName = "PXL_20230801_103507800.jpg",
                            contentUri = "",
                            md5 = Md5Hash(""),
                        ),
                        ProcessingItem(
                            localItemId = 6,
                            displayName = "PXL_20230801_103507100.jpg",
                            contentUri = "",
                            md5 = Md5Hash(""),
                        ),
                        ProcessingItem(
                            localItemId = 7,
                            displayName = "PXL_20230801_103507100.jpg",
                            contentUri = "",
                            md5 = Md5Hash(""),
                        ),
                    ),
                ),
            ) {}
        }
    }
}