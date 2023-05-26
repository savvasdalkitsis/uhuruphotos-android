package com.savvasdalkitsis.uhuruphotos.foundation.worker.api.model

import androidx.work.WorkInfo

data class RefreshJobState(
    val status: WorkInfo.State,
    val progress: Int,
)