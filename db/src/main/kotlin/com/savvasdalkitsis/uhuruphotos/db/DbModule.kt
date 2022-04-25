package com.savvasdalkitsis.uhuruphotos.db

import android.content.Context
import com.savvasdalkitsis.uhuruphotos.db.auth.Token
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule {

    @Provides
    @Singleton
    fun driver(@ApplicationContext context: Context): SqlDriver =
        AndroidSqliteDriver(Database.Schema, context, "uhuruPhotos.db")

    @Provides
    @Singleton
    fun database(driver: SqlDriver) = Database(
        driver = driver,
        tokenAdapter = Token.Adapter(typeAdapter = EnumColumnAdapter())
    )
}