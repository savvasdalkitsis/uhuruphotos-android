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

package com.savvasdalkitsis.uhuruphotos.foundation.image.api.hdr

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo.COLOR_MODE_DEFAULT
import android.content.pm.ActivityInfo.COLOR_MODE_HDR
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE
import androidx.activity.ComponentActivity

fun Context.setHDR(enable: Boolean) {
    if (SDK_INT >= UPSIDE_DOWN_CAKE) {
        getActivity()?.window?.colorMode = when {
            enable -> COLOR_MODE_HDR
            else -> COLOR_MODE_DEFAULT
        }
    }
}

private tailrec fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}