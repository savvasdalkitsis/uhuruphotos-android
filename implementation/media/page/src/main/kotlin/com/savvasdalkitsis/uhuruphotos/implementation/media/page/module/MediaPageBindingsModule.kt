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
package com.savvasdalkitsis.uhuruphotos.implementation.media.page.module

import com.savvasdalkitsis.uhuruphotos.api.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.domain.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.navigation.MediaItemPageNavigationTarget
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaPageBindingsModule {

    @Binds
    @IntoSet
    abstract fun navigationTarget(navigationTarget: MediaItemPageNavigationTarget): NavigationTarget

    @Binds
    abstract fun mediaUseCase(mediaUseCase: MediaUseCase):
            com.savvasdalkitsis.uhuruphotos.api.media.page.domain.usecase.MediaUseCase
}