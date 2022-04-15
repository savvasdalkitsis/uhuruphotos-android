package com.savvasdalkitsis.librephotos.image.module

import com.savvasdalkitsis.librephotos.image.initializer.ImageInitializer
import com.savvasdalkitsis.librephotos.initializer.ApplicationCreated
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