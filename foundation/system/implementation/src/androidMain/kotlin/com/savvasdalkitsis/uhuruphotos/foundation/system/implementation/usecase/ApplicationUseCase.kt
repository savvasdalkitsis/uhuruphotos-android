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

import android.content.Context
import android.content.pm.PackageManager.PackageInfoFlags
import android.os.Build
import com.savvasdalkitsis.uhuruphotos.foundation.system.api.usecase.ApplicationUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class ApplicationUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) : ApplicationUseCase {
    private val pm = context.packageManager
    private val packageName = context.packageName

    override fun appVersion(): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(packageName, PackageInfoFlags.of(0)).versionName
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(packageName, 0).versionName
        }
}