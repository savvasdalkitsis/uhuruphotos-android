package com.savvasdalkitsis.uhuruphotos.feature.db.domain.implementation.initializer

import android.app.Application
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.implementation.denormalization.DenormalizationQueueProcessor
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class DbInitializer @Inject constructor(
    private val denormalizationQueueProcessor: DenormalizationQueueProcessor,
): ApplicationCreated {
    override fun onAppCreated(app: Application) {
        denormalizationQueueProcessor.processQueue()
    }
}