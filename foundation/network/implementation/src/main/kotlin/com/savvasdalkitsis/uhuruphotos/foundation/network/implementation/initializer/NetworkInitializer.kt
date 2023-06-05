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
package com.savvasdalkitsis.uhuruphotos.foundation.network.implementation.initializer

import android.app.Application
import com.pluto.Pluto
import com.pluto.plugins.logger.PlutoLoggerPlugin
import com.pluto.plugins.logger.PlutoTimberTree
import com.pluto.plugins.network.PlutoNetworkPlugin
import com.pluto.plugins.preferences.PlutoSharePreferencesPlugin
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import se.ansman.dagger.auto.AutoBindIntoSet
import timber.log.Timber
import javax.inject.Inject

@AutoBindIntoSet
class NetworkInitializer @Inject constructor(
) : ApplicationCreated {
    override fun onAppCreated(app: Application) {
        Pluto.Installer(app)
            .addPlugin(PlutoNetworkPlugin("network"))
            .addPlugin(PlutoLoggerPlugin("logger"))
            .addPlugin(PlutoSharePreferencesPlugin("sharedPref"))
            .install()
        Timber.plant(PlutoTimberTree())
    }
}