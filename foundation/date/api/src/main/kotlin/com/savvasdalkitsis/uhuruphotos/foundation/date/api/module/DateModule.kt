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
package com.savvasdalkitsis.uhuruphotos.foundation.date.api.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
class DateModule {

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
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DisplayingTimeFormat

    @Provides
    @ParsingDateFormat
    fun parsingDateFormat(): DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    @Provides
    @ParsingDateTimeFormat
    fun parsingDateTimeFormat(): DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'kk:mm:ssZZ")

    @Provides
    @DisplayingDateFormat
    fun displayingDateFormat(): DateTimeFormatter = DateTimeFormat.fullDate()

    @Provides
    @DisplayingDateTimeFormat
    fun displayingDateTimeFormat(): DateTimeFormatter = DateTimeFormat.fullDateTime()

    @Provides
    @DisplayingTimeFormat
    fun displayingTimeFormat(): DateTimeFormatter = DateTimeFormat.fullTime()

}
