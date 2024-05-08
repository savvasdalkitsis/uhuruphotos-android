package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.PlatformAuthModule
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.module.DbModule
import com.savvasdalkitsis.uhuruphotos.feature.download.domain.api.module.DownloadModule
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedCache
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedRepository
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.service.FeedService
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.module.CommonMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.module.RemoteMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.module.PortfolioModule
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.SettingsModule
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.module.UploadModule
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.module.WelcomeModule
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.module.PreferencesModule
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.module.WorkerModule

object FeedModule {
    
    private val feedService: FeedService by singleInstance {
        PlatformAuthModule.ktorfit.create()
    }

    private val feedCache: FeedCache by singleInstance {
        FeedCache()
    }

    val feedUseCase: FeedUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.usecase.FeedUseCase(
            feedRepository,
            feedCache,
            CommonMediaModule.mediaUseCase,
            feedWorkScheduler,
            DownloadModule.downloadUseCase,
            UploadModule.uploadUseCase,
            PreferencesModule.plainTextPreferences,
            WelcomeModule.welcomeUseCase,
            PortfolioModule.portfolioUseCase,
        )

    val feedRepository: FeedRepository
        get() = FeedRepository(
            DbModule.database,
            DbModule.database.remoteMediaCollectionsQueries,
            RemoteMediaModule.remoteMediaUseCase,
            feedService,
        )

    val feedWorkScheduler: FeedWorkScheduler
        get() = com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.FeedWorkScheduler(
            WorkerModule.workScheduleUseCase,
            WorkerModule.workerStatusUseCase,
            SettingsModule.settingsUseCase,
        )
}