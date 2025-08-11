package com.savvasdalkitsis.uhuruphotos.feature.db.domain.implementation.denormalization

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationQueries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class DenormalizationQueueProcessor @Inject constructor(
    private val denormalizationQueries: DenormalizationQueries,
    private val denormalizer: Denormalizer,
) {

    fun processQueue() {
        CoroutineScope(Dispatchers.IO).launch {
            denormalizationQueries.get()
                .asFlow()
                .mapToList(Dispatchers.IO)
                .distinctUntilChanged()
                .collectLatest { denormalizations ->
                    denormalizations.forEach { denormalization ->
                        if (denormalizer.process(denormalization)) {
                            denormalizationQueries.delete(denormalization.id)
                        }
                    }
                }
        }
    }
}