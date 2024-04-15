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
package com.savvasdalkitsis.uhuruphotos.foundation.launchers.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

inline fun onMain(crossinline action: suspend () -> Unit) = CoroutineScope(Dispatchers.Main).launch {
    action()
}

inline fun onIO(crossinline action: suspend () -> Unit) = CoroutineScope(Dispatchers.IO).launch {
    action()
}

suspend inline fun <T> awaitOnMain(crossinline action: suspend () -> T) = CoroutineScope(Dispatchers.Main).async {
    action()
}.await()

suspend inline fun <T> awaitOnIO(crossinline action: suspend () -> T) = CoroutineScope(Dispatchers.IO).async {
    action()
}.await()