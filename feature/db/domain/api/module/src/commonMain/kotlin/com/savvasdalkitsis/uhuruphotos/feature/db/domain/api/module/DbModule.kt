/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.module

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.adapter.primitive.FloatColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbumPeople
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbums
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.auth.Token
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.lightbox.LightboxDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaTrash
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.People
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.person.PersonPhotos
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.portfolio.Portfolio
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.portfolio.PortfolioItems
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.user.User
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance

object DbModule {

    val database: Database by singleInstance {
        Database(
            driver = PlatformDbModule.driver,
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
            userAlbumsAdapter = UserAlbums.Adapter(IntColumnAdapter, IntColumnAdapter),
            portfolioAdapter = Portfolio.Adapter(IntColumnAdapter),
            portfolioItemsAdapter = PortfolioItems.Adapter(IntColumnAdapter),
            lightboxDetailsAdapter = LightboxDetails.Adapter(IntColumnAdapter, IntColumnAdapter),
        )
    }
}