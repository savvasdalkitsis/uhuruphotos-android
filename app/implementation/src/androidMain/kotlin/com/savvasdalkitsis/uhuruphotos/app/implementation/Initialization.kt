package com.savvasdalkitsis.uhuruphotos.app.implementation

import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.navigation.AboutNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.navigation.AutoAlbumNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.album.user.view.implementation.navigation.UserAlbumNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.navigation.WebLoginNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.navigation.AutoAlbumsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.navigation.UserAlbumsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.navigation.DiscoverNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.download.domain.api.module.DownloadModule
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.navigation.EditNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.favourites.view.implementation.navigation.FavouritesNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.module.FeedModule
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.navigation.FeedNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.navigation.HeatMapNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.navigation.HiddenPhotosNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.navigation.HomeNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.navigation.LibraryNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.navigation.LightboxNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.navigation.LocalAlbumNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.module.LocalMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.notifications.domain.api.module.NotificationsModule
import com.savvasdalkitsis.uhuruphotos.feature.notifications.view.implementation.navigation.NotificationsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.navigation.PeopleNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.navigation.PersonNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.navigation.PortfolioNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.processing.view.implementation.navigation.ProcessingNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.navigation.SearchNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.navigation.ServerNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.module.SettingsModule
import com.savvasdalkitsis.uhuruphotos.feature.stats.view.implementation.navigation.StatsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.module.SyncModule
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.navigation.TrashNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.undated.view.implementation.navigation.UndatedNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.uploads.view.implementation.navigation.UploadsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.videos.view.implementation.navigation.VideosNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.navigation.WelcomeNavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.activity.implementation.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.module.ImageModule
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ActivityCreated
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ActivityInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.module.LogModule
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.module.MapModule
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.module.NotificationModule
import com.savvasdalkitsis.uhuruphotos.foundation.tracing.implementation.initializer.TracingInitializer

object Initialization {

    val applicationInitializer: ApplicationCreated by singleInstance {
        ApplicationInitializer(
            AboutNavigationTarget,
            AutoAlbumNavigationTarget,
            AutoAlbumsNavigationTarget,
            DateModule.dateInitializer,
            DiscoverNavigationTarget,
            DownloadModule.downloadInitializer,
            EditNavigationTarget,
            FavouritesNavigationTarget,
            FeedNavigationTarget,
            FeedModule.feedInitializer,
            HeatMapNavigationTarget,
            HiddenPhotosNavigationTarget,
            HomeNavigationTarget,
            ImageModule.imageInitializer,
            LibraryNavigationTarget,
            LightboxNavigationTarget,
            LocalAlbumNavigationTarget,
            LogModule.logInitializer,
            MapModule.mapBoxInitializer,
            MapModule.maplibreInitializer,
            NotificationModule.notificationInitializer,
            NotificationsNavigationTarget,
            PeopleNavigationTarget,
            PersonNavigationTarget,
            PortfolioNavigationTarget,
            ProcessingNavigationTarget,
            SearchNavigationTarget,
            ServerNavigationTarget,
            SettingsModule.settingsNavigationTarget,
            StatsNavigationTarget,
            SyncModule.syncInitializer,
            TracingInitializer,
            TrashNavigationTarget,
            UndatedNavigationTarget,
            UploadsNavigationTarget,
            UserAlbumNavigationTarget,
            UserAlbumsNavigationTarget,
            VideosNavigationTarget,
            WebLoginNavigationTarget,
            WelcomeNavigationTarget,
        )
    }

    val activityInitializer: ActivityCreated by singleInstance {
        ActivityInitializer(
            AndroidModule.currentActivityHolder,
            DownloadModule.downloadActivityInitializer,
            LocalMediaModule.localMediaInitializer,
            NotificationsModule.notificationActivityInitializer,
        )
    }
}