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
package com.savvasdalkitsis.uhuruphotos.notification.module

import com.savvasdalkitsis.uhuruphotos.api.initializer.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.notification.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.notification.initializer.NotificationInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NotificationBindingsModule {

    @Binds
    @IntoSet
    abstract fun notificationInitializer(initializer: NotificationInitializer): ApplicationCreated

    @Binds
    abstract fun foregroundInfoBuilder(foregroundInfoBuilder: ForegroundInfoBuilder):
            com.savvasdalkitsis.uhuruphotos.api.notification.ForegroundInfoBuilder
}