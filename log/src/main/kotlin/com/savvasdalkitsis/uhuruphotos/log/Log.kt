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
package com.savvasdalkitsis.uhuruphotos.log

import android.util.Log
import timber.log.Timber


inline fun log(tag: String = "", msg: () -> String) {
    if (BuildConfig.DEBUG) {
        if (tag.isNotEmpty()) {
            Timber.tag(tag).log(Log.VERBOSE, msg())
        } else {
            Timber.log(Log.VERBOSE, msg())
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun log(t: Throwable) {
    if (BuildConfig.DEBUG) {
        Timber.log(Log.WARN, t)
    }
}