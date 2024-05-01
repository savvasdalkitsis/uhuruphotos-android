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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.module.FeedModule
import kotlin.LazyThreadSafetyMode.NONE

class CancelPrecacheWorkBroadcastReceiver: BroadcastReceiver() {

    private val feedWorkScheduler: FeedWorkScheduler by lazy(NONE) {
        FeedModule.feedWorkScheduler
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        feedWorkScheduler.cancelPrecacheThumbnails()
    }
}