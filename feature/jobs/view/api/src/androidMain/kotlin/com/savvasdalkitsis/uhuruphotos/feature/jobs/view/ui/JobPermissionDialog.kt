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
package com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job.FEED_DETAILS_SYNC
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job.FEED_SYNC
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job.LOCAL_MEDIA_SYNC
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job.PRECACHE_THUMBNAILS
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.YesNoDialog

@Composable
fun JobPermissionDialog(
    job: Job,
    onStartJob: (Job) -> Unit,
    onDismiss: () -> Unit,
) {
    YesNoDialog(
        title = stringResource(when(job) {
            FEED_SYNC -> string.perform_full_feed_sync
            PRECACHE_THUMBNAILS -> string.precache_thumbnails
            LOCAL_MEDIA_SYNC -> string.scan_local_media
            FEED_DETAILS_SYNC -> string.perform_full_feed_details_sync
        }),
        onDismiss = onDismiss,
        onYes = { onStartJob(job) },
    ) {
        Column {
            Text(stringResource(when(job) {
                FEED_SYNC -> string.are_you_sure_you_want_to_perform_full_sync
                PRECACHE_THUMBNAILS -> string.are_you_sure_you_want_to_perform_precache
                LOCAL_MEDIA_SYNC -> string.are_you_sure_you_want_to_start_local_scan
                FEED_DETAILS_SYNC -> string.are_you_sure_you_want_to_perform_feed_details_sync
            }))
            if (job != LOCAL_MEDIA_SYNC) {
                Text(
                    stringResource(string.process_takes_significant_time_consumes_battery),
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}
