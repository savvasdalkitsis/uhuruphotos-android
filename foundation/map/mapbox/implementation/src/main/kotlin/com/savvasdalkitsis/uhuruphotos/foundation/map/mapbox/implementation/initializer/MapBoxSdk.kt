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
package com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.initializer

import android.content.Context
import androidx.startup.AppInitializer
import com.mapbox.common.MapboxOptions
import com.mapbox.maps.loader.MapboxMapsInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.extensions.getMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class MapBoxSdk(
    private val context: Context,
    private val load: (Context) -> Unit,
    private val background: CoroutineDispatcher,
) {

    @Inject constructor(@ApplicationContext context: Context) : this(context, ::loadMapBox, Dispatchers.IO)

    private val loading = lazy { load(context) }

    fun ensureLoaded() = loading.value

    fun preload() {
        CoroutineScope(background).launch { runCatching { ensureLoaded() } }
    }
}

private fun loadMapBox(context: Context) {
    AppInitializer.getInstance(context).initializeComponent(MapboxMapsInitializer::class.java)
    MapboxOptions.accessToken = context.getMetadata("com.mapbox.API_KEY", "DUMMY_KEY")
}
