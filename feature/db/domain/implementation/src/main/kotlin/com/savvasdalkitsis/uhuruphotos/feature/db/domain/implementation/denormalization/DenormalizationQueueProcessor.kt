package com.savvasdalkitsis.uhuruphotos.feature.db.domain.implementation.denormalization

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationQueries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class DenormalizationQueueProcessor @Inject constructor(
    private val denormalizationQueries: DenormalizationQueries,
    private val denormalizer: Denormalizer,
) {

    fun processQueue() {
        val dispatcher = Dispatchers.IO.limitedParallelism(1, "denormalization")
        CoroutineScope(dispatcher).launch {
            denormalizationQueries.get()
                .asFlow()
                .mapToList(dispatcher)
                .distinctUntilChanged()
                .onEach { denormalizations ->
                    denormalizations.forEach { denormalization ->
                        if (denormalizer.process(denormalization)) {
                            denormalizationQueries.delete(denormalization.id)
                        }
                    }
                }
                .collect()
        }
    }
}