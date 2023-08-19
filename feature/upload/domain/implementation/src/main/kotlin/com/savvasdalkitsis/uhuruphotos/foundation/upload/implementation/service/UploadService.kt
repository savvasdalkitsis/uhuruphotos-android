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

import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.service.model.UploadChunkResult
import okhttp3.MultipartBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import se.ansman.dagger.auto.retrofit.AutoProvideService
import javax.inject.Singleton

@AutoProvideService
@Singleton
interface UploadService {

    @Multipart
    @POST("/api/upload/")
    suspend fun uploadChunk(
        @Header("Content-Range") range: String,
        @Part uploadId: MultipartBody.Part,
        @Part user: MultipartBody.Part,
        @Part offset: MultipartBody.Part,
        @Part md5: MultipartBody.Part,
        @Part chunk: MultipartBody.Part,
    ): UploadChunkResult

    @Multipart
    @POST("/api/upload/complete/")
    suspend fun completeUpload(
        @Part uploadId: MultipartBody.Part,
        @Part user: MultipartBody.Part,
        @Part filename: MultipartBody.Part,
        @Part md5: MultipartBody.Part,
    )
}