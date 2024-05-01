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
package com.savvasdalktsis.uhuruphotos.feature.download.domain.implementation.initializer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ActivityCreated
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log

class DownloadActivityInitializer(
    private val localMediaWorkScheduler: LocalMediaWorkScheduler,
): ActivityCreated {

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            log { "Notified of download completion. Starting local media sync" }
            localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
        }
    }

    override fun onActivityCreated(activity: FragmentActivity) {
        ContextCompat.registerReceiver(activity, receiver,
            IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }
    override fun onActivityDestroyed(activity: FragmentActivity) {
        activity.unregisterReceiver(receiver)
    }
}