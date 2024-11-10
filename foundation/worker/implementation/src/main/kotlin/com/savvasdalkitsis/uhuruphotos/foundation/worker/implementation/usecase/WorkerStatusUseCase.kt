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
package com.savvasdalkitsis.uhuruphotos.foundation.worker.implementation.usecase

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onMain
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkerStatusUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class WorkerStatusUseCase @Inject constructor(
    private val workManager: WorkManager,
) : WorkerStatusUseCase {

    override fun monitorUniqueJobStatus(jobName: String): Flow<WorkInfo.State?> =
        monitorUniqueJob(jobName).map { it?.state }

    override fun monitorUniqueJob(jobName: String): Flow<WorkInfo?> = queryWork {
        val data: LiveData<List<WorkInfo>> = workManager.getWorkInfosForUniqueWorkLiveData(jobName)
        data
    }.map { it.firstOrNull() }

    override fun monitorUniqueJobsByTag(tag: String): Flow<List<WorkInfo?>> = queryWork {
        workManager.getWorkInfosLiveData(WorkQuery.fromTags(tag))
    }

    private fun queryWork(query: () -> LiveData<List<WorkInfo>>): Flow<List<WorkInfo?>> {
        var observer: ((List<WorkInfo?>) -> Unit)?
        var liveData: LiveData<List<WorkInfo>>?
        return channelFlow {
            observer = {
                val workInfo = it
                CoroutineScope(Dispatchers.Default).launch {
                    send(workInfo)
                }
            }
            liveData = query()
            onMain {
                liveData!!.observeForever(observer!!)
            }
            awaitClose {
                onMain {
                    liveData!!.removeObserver(observer!!)
                }
            }
        }.cancellable()
    }
}