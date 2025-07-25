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
import android.os.StrictMode
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.savvasdalkitsis.uhuruphotos.BuildConfig
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationInitializer
import dagger.hilt.android.HiltAndroidApp
import se.ansman.dagger.auto.AutoDaggerInitializer
import javax.inject.Inject

@HiltAndroidApp
class App :
    Application(),
    Configuration.Provider {

    @Inject
    lateinit var initializer: AutoDaggerInitializer
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    @Inject
    lateinit var applicationInitializer: ApplicationInitializer

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG){
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
        initializer.initialize()
        applicationInitializer.onCreated(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}