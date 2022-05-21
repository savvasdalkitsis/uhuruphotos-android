package com.savvasdalkitsis.uhuruphotos.albums

import app.cash.turbine.test
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class ProgressUpdate : suspend (Int) -> Unit {
    private val progress = Channel<Int> {  }

    override suspend fun invoke(value: Int) {
        progress.send(value)
    }

    suspend fun assertReceived(value: Int) {
        progress.receiveAsFlow().test {
            assert(awaitItem() == value)
        }
    }
}