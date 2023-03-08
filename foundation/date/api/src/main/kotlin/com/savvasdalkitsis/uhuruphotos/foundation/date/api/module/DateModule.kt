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

    @Provides
    @ParsingDateFormat
    fun parsingDateFormat(): DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    @Provides
    @ParsingDateTimeFormat
    fun parsingDateTimeFormat(): DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'kk:mm:ss'Z'")

    @Provides
    @DisplayingDateFormat
    fun displayingDateFormat(): DateTimeFormatter = DateTimeFormat.fullDate()

    @Provides
    @DisplayingDateTimeFormat
    fun displayingDateTimeFormat(): DateTimeFormatter = DateTimeFormat.fullDateTime()

}