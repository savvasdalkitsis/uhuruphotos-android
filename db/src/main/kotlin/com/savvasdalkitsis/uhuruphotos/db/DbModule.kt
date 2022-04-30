package com.savvasdalkitsis.uhuruphotos.db

import android.content.Context
import com.savvasdalkitsis.uhuruphotos.db.albums.AlbumsQueries
import com.savvasdalkitsis.uhuruphotos.db.auth.Token
import com.savvasdalkitsis.uhuruphotos.db.auth.TokenQueries
import com.savvasdalkitsis.uhuruphotos.db.people.PeopleQueries
import com.savvasdalkitsis.uhuruphotos.db.person.PersonQueries
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoDetailsQueries
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoSummaryQueries
import com.savvasdalkitsis.uhuruphotos.db.search.SearchQueries
import com.savvasdalkitsis.uhuruphotos.db.user.UserQueries
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

    @Provides
    fun personQueries(database: Database): PersonQueries = database.personQueries

    @Provides
    fun albumsQueries(database: Database): AlbumsQueries = database.albumsQueries

    @Provides
    fun tokenQueries(database: Database): TokenQueries = database.tokenQueries

    @Provides
    fun peopleQueries(database: Database): PeopleQueries = database.peopleQueries

    @Provides
    fun userQueries(database: Database): UserQueries = database.userQueries

    @Provides
    fun photoDetailsQueries(database: Database): PhotoDetailsQueries = database.photoDetailsQueries

    @Provides
    fun photoSummaryQueries(database: Database): PhotoSummaryQueries = database.photoSummaryQueries

    @Provides
    fun searchQueries(database: Database): SearchQueries = database.searchQueries
}