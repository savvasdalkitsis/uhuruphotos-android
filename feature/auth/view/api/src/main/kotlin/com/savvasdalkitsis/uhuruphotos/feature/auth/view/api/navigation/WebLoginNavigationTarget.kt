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
package com.savvasdalkitsis.uhuruphotos.feature.auth.view.api.navigation

import android.util.Base64
import androidx.navigation.NavBackStackEntry

class WebLoginNavigationTarget {

    companion object {
        const val registrationName = "web-login/{url}"
        fun name(url: String) = registrationName.replace(
            "{url}", Base64.encodeToString(url.toByteArray(), Base64.URL_SAFE)
        )
        val NavBackStackEntry.url : String get() {
            val encodedUrl = arguments!!.getString("url")!!
            val url = android.util.Base64.decode(encodedUrl, android.util.Base64.URL_SAFE)
            return String(url)
        }
    }
}