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
package com.savvasdalkitsis.uhuruphotos.implementation.auth.weblogin.weblogin.seam

import com.savvasdalkitsis.uhuruphotos.api.seam.Mutation
import com.savvasdalkitsis.uhuruphotos.implementation.auth.weblogin.weblogin.view.WebLoginState

sealed class WebLoginMutation(
    mutation: Mutation<WebLoginState>,
) : Mutation<WebLoginState> by mutation {

    data class LoadPage(val url: String) : WebLoginMutation({
        it.copy(url = url)
    })

    object Loading : WebLoginMutation({
        it.copy(url = null)
    })
}