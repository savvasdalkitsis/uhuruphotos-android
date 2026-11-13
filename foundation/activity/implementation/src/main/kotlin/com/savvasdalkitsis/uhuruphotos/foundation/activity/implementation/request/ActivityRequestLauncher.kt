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
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.savedstate.SavedStateRegistry.SavedStateProvider
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.holder.CurrentActivityHolder
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.request.ActivityRequestFailed
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.request.ActivityRequestLauncher
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.suspendCancellableCoroutine
import se.ansman.dagger.auto.AutoBind
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.coroutines.resume

@AutoBind
@ActivityRetainedScoped
class ActivityRequestLauncher @Inject constructor(
    private val currentActivityHolder: CurrentActivityHolder
) : ActivityRequestLauncher {

    private val keyIncrement = AtomicInteger(0)
    private val noActivityResult get() = Err(
        IllegalStateException("No active activity found when trying to launch request")
    )
    private val savedStateRegistryKey = "SAVED_STATE_REGISTRY_KEY"
    private val requestKey = "REQUEST_KEY"
    private val lastIncrementKey = "LAST_INCREMENT_KEY"

    override suspend fun performRequest(
        requestId: String,
        request: IntentSenderRequest,
    ): SimpleResult {
        val activity = currentActivityHolder.currentActivity ?: return noActivityResult
        val restoredIncrement = activity.savedStateRegistry.consumeRestoredStateForKey(savedStateRegistryKey)
            ?.takeIf { it.getString(requestKey) == requestId }
            ?.getInt(lastIncrementKey)
        val increment = restoredIncrement ?: keyIncrement.getAndIncrement()
        val provider = prepareSavedData(activity, requestId, increment)
        var launcher: ActivityResultLauncher<IntentSenderRequest>? = null
        return try {
            suspendCancellableCoroutine { continuation ->
                launcher = activity.activityResultRegistry.register(
                    generateKey(increment),
                    ActivityResultContracts.StartIntentSenderForResult()
                ) { result ->
                    continuation.resume(result.toSimpleResult())
                }
                if (restoredIncrement == null) {
                    launcher!!.launch(request)
                }
            }
        } finally {
            launcher?.unregister()
            clearSavedStateData(activity, provider)
        }
    }

    private fun ActivityResult.toSimpleResult(): SimpleResult =
        if (resultCode == Activity.RESULT_OK) {
            Ok(Unit)
        } else {
            Err(ActivityRequestFailed(resultCode.toString()))
        }

    private fun prepareSavedData(
        currentActivity: ComponentActivity,
        requestId: String,
        increment: Int,
    ): SavedStateProvider {
        val provider = SavedStateProvider {
            bundleOf(
                requestKey to requestId,
                lastIncrementKey to increment,
            )
        }
        with(currentActivity.savedStateRegistry) {
            unregisterSavedStateProvider(savedStateRegistryKey)
            registerSavedStateProvider(savedStateRegistryKey, provider)
        }
        return provider
    }

    private fun clearSavedStateData(currentActivity: ComponentActivity, provider: SavedStateProvider) =
        with(currentActivity.savedStateRegistry) {
            if (getSavedStateProvider(savedStateRegistryKey) === provider) {
                unregisterSavedStateProvider(savedStateRegistryKey)
            }
        }

    private fun generateKey(increment: Int) = "request_$increment"
}
