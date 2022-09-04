package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.initializer

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import javax.inject.Inject

internal class FeedInitializer @Inject constructor(
    private val feedWorkScheduler: FeedWorkScheduler,
): ApplicationCreated {

    override fun onAppCreated(app: Application) {
        feedWorkScheduler.scheduleFeedRefreshPeriodic(
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP
        )
    }

}