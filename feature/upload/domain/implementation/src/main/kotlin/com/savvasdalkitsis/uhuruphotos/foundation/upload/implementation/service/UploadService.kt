/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.service

import android.content.Context
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationHeadersUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import javax.inject.Inject

class UploadService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val serverUseCase: ServerUseCase,
    private val authenticationHeadersUseCase: AuthenticationHeadersUseCase,
) {

    fun upload(contentUri: String): Boolean {
        val baseUrl = serverUseCase.getServerUrl() ?: return false
        val url = "$baseUrl/api/upload/"
        val headers = authenticationHeadersUseCase.headers(url)
        MultipartUploadRequest(context, baseUrl)
            .addFileToUpload(contentUri, parameterName = "file", fileName = "blob")
            .run {
                headers.fold(this) { c, (key, value) -> c.addHeader(key, value) }
            }
            .setMethod("POST")
            .startUpload()
        return true
    }
}