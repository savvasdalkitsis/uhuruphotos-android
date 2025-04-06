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
package com.savvasdalkitsis.uhuruphotos.feature.uploads.view.implementation.ui

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJob
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.Failed
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.Finished
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.InQueue
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.Processing
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.Uploading
import com.savvasdalkitsis.uhuruphotos.feature.uploads.view.implementation.ui.state.UploadsState
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.Thumbnail
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.CustomColors
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UhuruFullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruUpNavButton
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun Uploads(
    state: UploadsState,
) {
    UhuruScaffold(
        title = { Text(text = "${stringResource(string.uploads)} (${state.jobs.size})") },
        navigationIcon = { UhuruUpNavButton() },
    ) { contentPadding ->
        when {
            state.isLoading -> UhuruFullLoading()
            state.jobs.isEmpty() -> UhuruFullLoading {
                UhuruIcon(icon = R.raw.animation_empty)
            }
            else -> LazyColumn(
                modifier = Modifier.padding(contentPadding),
            ) {
                for (job in state.jobs) {
                    item(job.localItemId) {
                        UploadJobRow(job)
                    }
                }
            }
        }
    }
}

@Composable
fun UploadJobRow(job: UploadJob) {
    val status = job.status
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
            url = job.contentUri,
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = spacedBy(4.dp),
        ) {
            Text(
                text = job.displayName ?: "",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                modifier = Modifier
                    .height(4.dp),
                horizontalArrangement = spacedBy(2.dp),
            ) {
                Segment(status)
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (status is Uploading) {
                    Text(
                        text = status.progressDisplay,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(status.displayName),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun RowScope.Segment(
    status: UploadStatus,
) {
    when(status) {
        InQueue -> LinearProgressIndicator(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            strokeCap = StrokeCap.Round,
            color = CustomColors.syncQueued,
        )
        is Uploading -> {
            val progress by animateFloatAsState(status.progressPercent)
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                progress = { progress },
                strokeCap = StrokeCap.Round,
            )
        }
        Processing -> {
            Box(modifier = Modifier
                .fillMaxHeight()
                .weight(0.8f)
                .background(CustomColors.syncSuccess, RoundedCornerShape(2.dp))
            )
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.2f),
                strokeCap = StrokeCap.Round,
                color = CustomColors.syncQueued,
            )
        }
        is Failed -> Box(modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .background(CustomColors.syncError, RoundedCornerShape(2.dp))
        )
        Finished -> Box(modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .background(CustomColors.syncSuccess, RoundedCornerShape(2.dp))
        )
    }
}

@Preview
@Composable
private fun UploadsPreview() {
    PreviewAppTheme {
        CompositionLocalProvider(
            LocalThumbnailImageLoader provides ImageLoader(LocalContext.current)
        ) {
            Uploads(
                UploadsState(
                    isLoading = false,
                    jobs = persistentListOf(
                        UploadJob(
                            localItemId = 1,
                            displayName = "PXL_20230801_103507882.jpg",
                            contentUri = "",
                            status = Uploading(0.2f, "20.00%"),
                        ),
                        UploadJob(
                            localItemId = 2,
                            displayName = "PXL_20230801_103507850.jpg",
                            contentUri = "",
                            status = InQueue,
                        ),
                        UploadJob(
                            localItemId = 3,
                            displayName = "PXL_20230801_103507810.mp4",
                            contentUri = "",
                            status = InQueue,
                        ),
                        UploadJob(
                            localItemId = 30,
                            displayName = "PXL_20230801_103507810.mp4",
                            contentUri = "",
                            status = InQueue,
                        ),
                        UploadJob(
                            localItemId = 4,
                            displayName = "PXL_20230801_103507810.mp4",
                            contentUri = "",
                            status = Failed(""),
                        ),
                        UploadJob(
                            localItemId = 5,
                            displayName = "PXL_20230801_103507800.jpg",
                            contentUri = "",
                            status = InQueue,
                        ),
                        UploadJob(
                            localItemId = 6,
                            displayName = "PXL_20230801_103507100.jpg",
                            contentUri = "",
                            status = Processing,
                        ),
                        UploadJob(
                            localItemId = 7,
                            displayName = "PXL_20230801_103507100.jpg",
                            contentUri = "",
                            status = Finished,
                        ),
                    ),
                )
            )
        }
    }
}