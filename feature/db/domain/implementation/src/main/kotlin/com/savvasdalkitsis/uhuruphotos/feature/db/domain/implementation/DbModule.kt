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
package com.savvasdalkitsis.uhuruphotos.feature.db.domain.implementation

import android.content.Context
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.adapter.primitive.FloatColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbumPeople
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbumPeopleQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbumPhotosQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbumQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbums
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbumsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbumPhotosQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbumQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbumsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.auth.Token
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.auth.TokenQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.download.DownloadingMediaItemsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollectionsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummaryQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaTrash
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaTrashQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.upload.UploadIdsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.People
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.upload.UploadingMediaItemsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.PeopleQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.person.PersonPhotos
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.person.PersonQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.search.SearchQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.user.User
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.user.UserQueries
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
        tokenAdapter = Token.Adapter(typeAdapter = EnumColumnAdapter()),
        autoAlbumPeopleAdapter = AutoAlbumPeople.Adapter(IntColumnAdapter),
        autoAlbumsAdapter = AutoAlbums.Adapter(IntColumnAdapter, IntColumnAdapter),
        localMediaItemDetailsAdapter = LocalMediaItemDetails.Adapter(IntColumnAdapter, IntColumnAdapter, IntColumnAdapter, IntColumnAdapter, IntColumnAdapter),
        peopleAdapter = People.Adapter(IntColumnAdapter, IntColumnAdapter),
        personPhotosAdapter = PersonPhotos.Adapter(IntColumnAdapter),
        remoteMediaCollectionsAdapter = RemoteMediaCollections.Adapter(IntColumnAdapter, IntColumnAdapter),
        remoteMediaItemDetailsAdapter = RemoteMediaItemDetails.Adapter(IntColumnAdapter),
        remoteMediaItemSummaryAdapter = RemoteMediaItemSummary.Adapter(FloatColumnAdapter, IntColumnAdapter),
        remoteMediaTrashAdapter =  RemoteMediaTrash.Adapter(FloatColumnAdapter, IntColumnAdapter),
        userAdapter = User.Adapter(IntColumnAdapter, IntColumnAdapter, FloatColumnAdapter, IntColumnAdapter, IntColumnAdapter, IntColumnAdapter, IntColumnAdapter),
        userAlbumsAdapter = UserAlbums.Adapter(IntColumnAdapter, IntColumnAdapter)
    )

    @Provides
    fun personQueries(database: Database): PersonQueries = database.personQueries

    @Provides
    fun remoteMediaCollectionsQueries(database: Database): RemoteMediaCollectionsQueries = database.remoteMediaCollectionsQueries

    @Provides
    fun tokenQueries(database: Database): TokenQueries = database.tokenQueries

    @Provides
    fun peopleQueries(database: Database): PeopleQueries = database.peopleQueries

    @Provides
    fun userQueries(database: Database): UserQueries = database.userQueries

    @Provides
    fun photoDetailsQueries(database: Database): RemoteMediaItemDetailsQueries = database.remoteMediaItemDetailsQueries

    @Provides
    fun photoSummaryQueries(database: Database): RemoteMediaItemSummaryQueries = database.remoteMediaItemSummaryQueries

    @Provides
    fun trashQueries(database: Database): RemoteMediaTrashQueries = database.remoteMediaTrashQueries

    @Provides
    fun searchQueries(database: Database): SearchQueries = database.searchQueries

    @Provides
    fun autoAlbumsQueries(database: Database): AutoAlbumsQueries = database.autoAlbumsQueries

    @Provides
    fun userAlbumsQueries(database: Database): UserAlbumsQueries = database.userAlbumsQueries

    @Provides
    fun autoAlbumQueries(database: Database): AutoAlbumQueries = database.autoAlbumQueries

    @Provides
    fun userAlbumQueries(database: Database): UserAlbumQueries = database.userAlbumQueries

    @Provides
    fun userAlbumPhotosQueries(database: Database): UserAlbumPhotosQueries = database.userAlbumPhotosQueries

    @Provides
    fun autoAlbumPhotosQueries(database: Database): AutoAlbumPhotosQueries = database.autoAlbumPhotosQueries

    @Provides
    fun autoAlbumPeopleQueries(database: Database): AutoAlbumPeopleQueries = database.autoAlbumPeopleQueries

    @Provides
    fun localMediaItemDetailsQueries(database: Database): LocalMediaItemDetailsQueries = database.localMediaItemDetailsQueries

    @Provides
    fun downloadingMediaItemsQueries(database: Database): DownloadingMediaItemsQueries = database.downloadingMediaItemsQueries

    @Provides
    fun uploadingMediaItemsQueries(database: Database): UploadingMediaItemsQueries = database.uploadingMediaItemsQueries

    @Provides
    fun uploadIdsQueries(database: Database): UploadIdsQueries = database.uploadIdsQueries
}