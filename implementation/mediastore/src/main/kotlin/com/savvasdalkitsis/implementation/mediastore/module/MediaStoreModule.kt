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
package com.savvasdalkitsis.implementation.mediastore.module

import android.annotation.SuppressLint
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.DateFormat
import java.text.SimpleDateFormat

@dagger.Module
@InstallIn(SingletonComponent::class)
internal class MediaStoreModule {

    @Retention(AnnotationRetention.BINARY)
    annotation class MediaStoreDateTimeFormat

    @SuppressLint("SimpleDateFormat")
    @Provides
    @MediaStoreDateTimeFormat
    fun mediaStoreDateTimeFormat(): DateFormat =
        SimpleDateFormat("yyy:MM:dd kk:mm:ss")

}