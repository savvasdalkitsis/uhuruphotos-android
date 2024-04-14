/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.battery.domain.implementation.repository

import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import javax.inject.Inject

class BatteryRepository @Inject constructor(
    @PlainTextPreferences
    private val preferences: Preferences,
) {

    private val key = "keyBatteryOptimizationShouldCheck"

    fun shouldCheckBatteryOptimization(): Boolean = preferences.get(key, true)

    fun doNotCheckBatteryOptimization() {
        preferences.set(key, false)
    }
}