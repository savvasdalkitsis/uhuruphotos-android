package com.savvasdalkitsis.uhuruphotos.photos.module

import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.photos.navigation.PhotoNavigationTarget
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class PhotosBindingsModule {

    @Binds
    @IntoSet
    abstract fun navigationTarget(navigationTarget: PhotoNavigationTarget): NavigationTarget
}