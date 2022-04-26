package com.savvasdalkitsis.uhuruphotos.notification.module

import com.savvasdalkitsis.uhuruphotos.initializer.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.notification.initializer.NotificationInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationBindingsModule {

    @Binds
    @IntoSet
    abstract fun notificationInitializer(initializer: NotificationInitializer): ApplicationCreated
}