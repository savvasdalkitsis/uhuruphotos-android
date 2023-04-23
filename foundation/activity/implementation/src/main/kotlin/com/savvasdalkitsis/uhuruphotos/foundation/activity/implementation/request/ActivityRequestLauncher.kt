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
package com.savvasdalkitsis.uhuruphotos.foundation.activity.implementation.request

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.holder.CurrentActivityHolder
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.request.ActivityRequestFailed
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.request.ActivityRequestLauncher
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.coroutines.resume

class ActivityRequestLauncher @Inject constructor(
    private val currentActivityHolder: CurrentActivityHolder
) : ActivityRequestLauncher {

    private val keyIncrement = AtomicInteger(0)
    private val activity get() = currentActivityHolder.currentActivity
    private val noActivityResult get() = Result.failure<Unit>(
        IllegalStateException("No active activity found when trying to launch request")
    )
    private val savedStateRegistryKey = "SAVED_STATE_REGISTRY_KEY"
    private val requestKey = "REQUEST_KEY"
    private val lastIncrementKey = "LAST_INCREMENT_KEY"

    override suspend fun performRequest(
        requestId: String,
        request: IntentSenderRequest,
        onSuccess: () -> Unit
    ): Result<Unit> {
        var isLaunched = false

        val key = activity?.let { activity ->
            val savedBundle = activity.savedStateRegistry.consumeRestoredStateForKey(savedStateRegistryKey)
            if (savedBundle?.getString(requestKey) == requestId) {
                isLaunched = true
                generateKey(savedBundle.getInt(lastIncrementKey))
            } else {
                generateKey(keyIncrement.getAndIncrement())
            }
        } ?: return noActivityResult

        activity?.let {
            if (!isLaunched) {
                prepareSavedData(it, requestId)
            }
        } ?: return noActivityResult

        var launcher: ActivityResultLauncher<IntentSenderRequest>? = null
        return try {
            val activity = activity ?: return noActivityResult
            suspendCancellableCoroutine<Result<Unit>> { continuation ->
                launcher = activity.activityResultRegistry.register(
                    key,
                    ActivityResultContracts.StartIntentSenderForResult()
                ) { result ->
                    clearSavedStateData(activity)
                    continuation.resume(
                        if (result.resultCode == Activity.RESULT_OK) {
                            onSuccess()
                            Result.success(Unit)
                        } else {
                            Result.failure(ActivityRequestFailed(result.resultCode.toString()))
                        }
                    )
                }

                if (!isLaunched) {
                    launcher!!.launch(request)
                    isLaunched = true
                }
            }
        } finally {
            launcher?.unregister()
        }
    }

    private fun prepareSavedData(currentActivity: ComponentActivity, requestId: String) {
        currentActivity.savedStateRegistry.registerSavedStateProvider(
            savedStateRegistryKey
        ) {
            bundleOf(
                requestKey to requestId,
                lastIncrementKey to keyIncrement.get() - 1
            )
        }
    }

    private fun clearSavedStateData(currentActivity: ComponentActivity) =
        with(currentActivity.savedStateRegistry){
            unregisterSavedStateProvider(savedStateRegistryKey)
            consumeRestoredStateForKey(savedStateRegistryKey)
        }

    private fun generateKey(increment: Int) = "request_$increment"
}