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
package com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.initializer

import android.app.Application
import androidx.startup.AppInitializer
import com.mapbox.common.MapboxOptions
import com.mapbox.maps.loader.MapboxMapsInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.extensions.getMetadata
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
internal class MapsInitializer @Inject constructor(
) : ApplicationCreated {

    override fun onAppCreated(app: Application) {
        MainScope().launch(Dispatchers.IO) {
            AppInitializer.getInstance(app)
                .initializeComponent(MapboxMapsInitializer::class.java)
            MapboxOptions.accessToken = app.getMetadata("com.mapbox.API_KEY", "DUMMY_KEY")
        }
    }
}