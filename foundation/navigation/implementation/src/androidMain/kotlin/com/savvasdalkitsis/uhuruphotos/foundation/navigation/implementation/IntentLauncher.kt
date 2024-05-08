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
import android.net.Uri
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.ExternalNavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.ExternalNavigator
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase

class IntentLauncher(
    private val context: Context,
    private val toasterUseCase: ToasterUseCase,
) : ExternalNavigator {

    override fun navigateToWeb(webUrl: String) {
        navigateTo(Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)))
    }

    override fun navigateTo(externalTarget: ExternalNavigationTarget) {
        try {
            context.startActivity(externalTarget.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: ActivityNotFoundException) {
            log(e)
            toasterUseCase.show(strings.could_not_find_app_to_open)
        }
    }

    override fun navigateTo(
        externalTarget: ExternalNavigationTarget,
        fallbackExternalTarget: ExternalNavigationTarget,
    ) {
        try {
            context.startActivity(externalTarget.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: ActivityNotFoundException) {
            log(e)
            navigateTo(fallbackExternalTarget)
        }
    }

}
