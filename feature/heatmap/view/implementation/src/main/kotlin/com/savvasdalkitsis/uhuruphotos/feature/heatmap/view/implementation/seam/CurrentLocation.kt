/*
Copyright 2026 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.FUSED_PROVIDER
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.location.LocationManager.PASSIVE_PROVIDER
import android.os.CancellationSignal
import androidx.core.location.LocationManagerCompat
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@SuppressLint("InlinedApi")
private val freshFixProviders = listOf(FUSED_PROVIDER, NETWORK_PROVIDER, GPS_PROVIDER)

@SuppressLint("InlinedApi")
private val lastKnownProviders = listOf(FUSED_PROVIDER, GPS_PROVIDER, NETWORK_PROVIDER, PASSIVE_PROVIDER)

internal val currentLocationTimeout = 10.seconds

internal suspend fun LocationManager.awaitCurrentLocation(timeout: Duration = currentLocationTimeout): LatLon? =
    (withTimeoutOrNull(timeout) { firstFix() } ?: lastKnownLocation())
        ?.let { LatLon(it.latitude, it.longitude) }

private suspend fun LocationManager.firstFix(): Location? = channelFlow {
    availableProviders(freshFixProviders)
        .filter { isProviderEnabled(it) }
        .forEach { provider -> launch { send(currentFix(provider)) } }
}.filterNotNull().firstOrNull()

@SuppressLint("MissingPermission")
private suspend fun LocationManager.currentFix(provider: String): Location? =
    suspendCancellableCoroutine { continuation ->
        val cancellation = CancellationSignal()
        continuation.invokeOnCancellation { cancellation.cancel() }
        LocationManagerCompat.getCurrentLocation(this, provider, cancellation, Runnable::run) {
            continuation.resume(it)
        }
    }

@SuppressLint("MissingPermission")
private fun LocationManager.lastKnownLocation(): Location? =
    availableProviders(lastKnownProviders)
        .mapNotNull { getLastKnownLocation(it) }
        .maxByOrNull { it.time }

private fun LocationManager.availableProviders(providers: List<String>) =
    providers.filter { LocationManagerCompat.hasProvider(this, it) }
