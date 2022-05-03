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
package com.savvasdalkitsis.uhuruphotos.infrastructure.module

import android.annotation.SuppressLint
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
class InfrastructureModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ParsingDateFormat
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ParsingDateTimeFormat
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DisplayingDateFormat
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DisplayingDateTimeFormat

    @SuppressLint("SimpleDateFormat")
    @Provides
    @ParsingDateFormat
    fun parsingDateFormat(): DateFormat = SimpleDateFormat("yyy-MM-dd")

    @SuppressLint("SimpleDateFormat")
    @Provides
    @ParsingDateTimeFormat
    fun parsingDateTimeFormat(): DateFormat = SimpleDateFormat("yyy-MM-dd'T'kk:mm:ss'Z'")

    @Provides
    @DisplayingDateFormat
    fun displayingDateFormat(): DateFormat = DateFormat.getDateInstance(DateFormat.FULL)

    @Provides
    @DisplayingDateTimeFormat
    fun displayingDateTimeFormat(): DateFormat = DateFormat.getDateTimeInstance(
        DateFormat.FULL,
        DateFormat.FULL
    )

}