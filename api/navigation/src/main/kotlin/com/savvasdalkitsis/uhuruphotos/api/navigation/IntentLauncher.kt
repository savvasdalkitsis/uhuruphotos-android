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
package com.savvasdalkitsis.uhuruphotos.api.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.api.toaster.Toaster
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class IntentLauncher @Inject constructor(
    @ApplicationContext private val context: Context,
    private val toaster: Toaster,
) {

    fun launch(intent: Intent) {
        try {
            context.startActivity(intent.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: ActivityNotFoundException) {
            log(e)
            toaster.show(R.string.could_not_find_app_to_open)
        }
    }

}
