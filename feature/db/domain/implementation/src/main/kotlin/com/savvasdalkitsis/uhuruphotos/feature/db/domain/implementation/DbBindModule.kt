package com.savvasdalkitsis.uhuruphotos.feature.db.domain.implementation

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization.DenormalizationQueue
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.implementation.denormalization.DbDenormalizationQueue
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DbBindModule {

    @Binds
    fun denormalizer(denormalizer: DbDenormalizationQueue): DenormalizationQueue
}