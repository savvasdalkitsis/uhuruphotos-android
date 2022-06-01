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
package com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.mvflow

import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.view.WebLoginState
import com.savvasdalkitsis.uhuruphotos.viewmodel.Mutation

sealed class WebLoginMutation : Mutation<WebLoginState> {

    data class LoadPage(val url: String) : WebLoginMutation() {
        override fun reduce(state: WebLoginState) = state.copy(url = url)
    }

    object Loading : WebLoginMutation() {
        override fun reduce(state: WebLoginState) = state.copy(url = null)
    }
}