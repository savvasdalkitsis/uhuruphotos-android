package com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job.*
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

@Composable
fun JobPermissionDialog(
    job: Job,
    onStartJob: (Job) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large,
        title = { Text(stringResource(when(job) {
            FEED_SYNC -> string.perform_full_feed_sync
            PRECACHE_THUMBNAILS -> string.precache_thumbnails
            LOCAL_MEDIA_SYNC -> string.local_media_sync
        })) },
        text = {
            Column {
                Text(stringResource(when(job) {
                    FEED_SYNC -> string.are_you_sure_you_want_to_perform_full_sync
                    PRECACHE_THUMBNAILS -> string.are_you_sure_you_want_to_perform_precache
                    LOCAL_MEDIA_SYNC -> string.are_you_sure_you_want_to_start_local_sync
                }))
                if (job != LOCAL_MEDIA_SYNC) {
                    Text(
                        stringResource(string.process_takes_significant_time_consumes_battery),
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onStartJob(job) }) {
                Text(stringResource(string.yes))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(string.no))
            }
        },
    )
}