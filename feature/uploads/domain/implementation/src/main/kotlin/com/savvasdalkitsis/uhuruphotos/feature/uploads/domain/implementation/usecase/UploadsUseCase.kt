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
package com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.implementation.usecase

import android.content.Context
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.uploads.UploadsQueries
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJob
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.Failed
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.Finished
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.InQueue
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.Processing
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.Uploading
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatuses.FAILED
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatuses.FINISHED
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatuses.IN_QUEUE
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatuses.PROCESSING
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatuses.UPLOADING
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.model.Uploads
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.usecase.UploadsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class UploadsUseCase @Inject constructor(
    private val uploadsQueries: UploadsQueries,
    @ApplicationContext private val context: Context,
) : UploadsUseCase {

    override fun observeUploadsInFlight(): Flow<Uploads> =
        uploadsQueries.get()
            .asFlow().mapToList(Dispatchers.IO)
            .map { items ->
                Uploads(
                    jobs = items.map { item ->
                        UploadJob(
                            localItemId = item.id,
                            displayName = item.displayName,
                            contentUri = item.uri.resolve(
                                item.md5sum,
                                "",
                                null,
                                item.isVideo,
                                context,
                                isThumbnail = true,
                            ),
                            status = when (item.status) {
                                UPLOADING -> {
                                    val progress = item.progress ?: 0
                                    Uploading(
                                        progressPercent = progress / 100f,
                                        progressDisplay = "$progress% ",
                                    )
                                }
                                IN_QUEUE -> InQueue
                                PROCESSING -> Processing
                                FAILED -> Failed(item.lastResponse)
                                FINISHED -> Finished
                            }
                        )
                    }
                )
            }

    override suspend fun clearFinishedUploads() {
        uploadsQueries.clearUploadsOfStatus(FINISHED).await()
    }
}
