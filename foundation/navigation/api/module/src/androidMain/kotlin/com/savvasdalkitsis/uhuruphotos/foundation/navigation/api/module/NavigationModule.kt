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
package com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.IntentLauncher
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization.AndroidBase64Transcoder
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization.Base64Transcoder
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization.SealedClassTypeAdapterFactory
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.module.ToasterModule

object NavigationModule {

    val navigator: Navigator by singleInstance {
        com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.Navigator(
            intentLauncher
        )
    }

    val gson: Gson
        get() = GsonBuilder()
            .registerTypeAdapterFactory(SealedClassTypeAdapterFactory())
            .create()

    val base64Transcoder: Base64Transcoder
        get() = AndroidBase64Transcoder()

    private val intentLauncher get() =
        IntentLauncher(AndroidModule.applicationContext, ToasterModule.toasterUseCase)
}