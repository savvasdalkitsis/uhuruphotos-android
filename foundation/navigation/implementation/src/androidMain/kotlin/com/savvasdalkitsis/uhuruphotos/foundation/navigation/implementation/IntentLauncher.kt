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
package com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string.could_not_find_app_to_open
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class IntentLauncher @Inject constructor(
    @ApplicationContext private val context: Context,
    private val toasterUseCase: ToasterUseCase,
) {

    fun launch(intent: Intent) {
        try {
            context.startActivity(intent.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: ActivityNotFoundException) {
            log(e)
            toasterUseCase.show(could_not_find_app_to_open)
        }
    }
    fun launch(intent: Intent, fallbackIntent: Intent) {
        try {
            context.startActivity(intent.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: ActivityNotFoundException) {
            log(e)
            launch(fallbackIntent)
        }
    }

}
