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
package com.savvasdalkitsis.uhuruphotos.db

import android.content.Context
import com.savvasdalkitsis.uhuruphotos.db.albums.AlbumsQueries
import com.savvasdalkitsis.uhuruphotos.db.albums.AutoAlbumPeopleQueries
import com.savvasdalkitsis.uhuruphotos.db.albums.AutoAlbumPhotosQueries
import com.savvasdalkitsis.uhuruphotos.db.albums.AutoAlbumQueries
import com.savvasdalkitsis.uhuruphotos.db.albums.AutoAlbumsQueries
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

    @Provides
    fun autoAlbumsQueries(database: Database): AutoAlbumsQueries = database.autoAlbumsQueries

    @Provides
    fun autoAlbumQueries(database: Database): AutoAlbumQueries = database.autoAlbumQueries

    @Provides
    fun autoAlbumPhotosQueries(database: Database): AutoAlbumPhotosQueries = database.autoAlbumPhotosQueries

    @Provides
    fun autoAlbumPeopleQueries(database: Database): AutoAlbumPeopleQueries = database.autoAlbumPeopleQueries
}