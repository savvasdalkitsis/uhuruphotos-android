package com.savvasdalkitsis.uhuruphotos.albums.api.worker

import androidx.work.WorkInfo

data class RefreshJobState(
    val status: WorkInfo.State,
    val progress: Int,
)