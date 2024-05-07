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
package com.savvasdalkitsis.uhuruphotos.foundation.system.implementation.usecase

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.PowerManager
import android.os.StatFs
import com.savvasdalkitsis.uhuruphotos.foundation.system.api.usecase.BatteryOptimization
import com.savvasdalkitsis.uhuruphotos.foundation.system.api.usecase.BatteryOptimization.Supported
import com.savvasdalkitsis.uhuruphotos.foundation.system.api.usecase.SystemUseCase

class SystemUseCase(
    private val context: Context,
    private val powerManager: PowerManager,
    private val activityManager: ActivityManager,
) : SystemUseCase {

    override fun getAvailableSystemMemoryInMb(): Int =
        with(ActivityManager.MemoryInfo()) {
            activityManager.getMemoryInfo(this)
            (availMem / (1024 * 1024)).toInt()
        }

    override fun getAvailableStorageInMb(): Int =
        with(StatFs(Environment.getExternalStorageDirectory().path)) {
            (blockSizeLong * blockCountLong / (1024 * 1024)).toInt()
        }

    override fun isIgnoringBatteryOptimizations(): BatteryOptimization = Supported(
        !powerManager.isIgnoringBatteryOptimizations(context.packageName)
    )
}
