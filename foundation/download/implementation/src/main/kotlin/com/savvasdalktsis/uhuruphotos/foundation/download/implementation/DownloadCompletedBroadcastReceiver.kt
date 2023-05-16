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
package com.savvasdalktsis.uhuruphotos.foundation.download.implementation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalktsis.uhuruphotos.foundation.download.implementation.usecase.DownloadUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class DownloadCompletedBroadcastReceiver : BroadcastReceiver() {

    @Inject lateinit var localMediaWorkScheduler: LocalMediaWorkScheduler
    @Inject lateinit var downloadUseCase: DownloadUseCase

    override fun onReceive(context: Context, intent: Intent) {
        log { "Notified of download completion. Starting local media sync" }
        localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
    }
}