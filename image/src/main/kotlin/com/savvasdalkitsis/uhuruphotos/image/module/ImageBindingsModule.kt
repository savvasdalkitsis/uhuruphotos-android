package com.savvasdalkitsis.uhuruphotos.image.module

import com.savvasdalkitsis.uhuruphotos.image.initializer.ImageInitializer
import com.savvasdalkitsis.uhuruphotos.initializer.ApplicationCreated
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageBindingsModule {

    @Binds
    @IntoSet
    abstract fun imageInitializer(imageInitializer: ImageInitializer): ApplicationCreated
}