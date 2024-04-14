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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.initializer

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
internal class FeedInitializer @Inject constructor(
    private val feedWorkScheduler: FeedWorkScheduler,
): ApplicationCreated {

    override fun onAppCreated(app: Application) {
        feedWorkScheduler.scheduleFeedRefreshPeriodic(
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP
        )
    }

}
