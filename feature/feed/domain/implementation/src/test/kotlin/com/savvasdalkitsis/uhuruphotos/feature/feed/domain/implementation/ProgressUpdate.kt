/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation

import app.cash.turbine.test
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class ProgressUpdate : suspend (Int, Int) -> Unit {
    private val progress = Channel<Pair<Int, Int>> {  }

    override suspend fun invoke(current: Int, total: Int) {
        progress.send(current to total)
    }

    suspend fun assertReceived(value: Pair<Int, Int>) {
        progress.receiveAsFlow().test {
            assert(awaitItem() == value)
        }
    }
}