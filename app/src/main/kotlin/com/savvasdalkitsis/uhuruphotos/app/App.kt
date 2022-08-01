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
package com.savvasdalkitsis.uhuruphotos.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.savvasdalkitsis.uhuruphotos.api.initializer.ApplicationInitializer
import dagger.hilt.android.HiltAndroidApp
import dev.shreyaspatil.permissionFlow.PermissionFlow
import javax.inject.Inject

@HiltAndroidApp
class App :
    Application(),
    Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    @Inject
    lateinit var applicationInitializer: ApplicationInitializer

    override fun onCreate() {
        super.onCreate()
        PermissionFlow.init(this)
        applicationInitializer.onCreated(this)
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()
}