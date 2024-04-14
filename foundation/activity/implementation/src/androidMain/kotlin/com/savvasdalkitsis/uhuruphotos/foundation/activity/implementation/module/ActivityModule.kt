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
package com.savvasdalkitsis.uhuruphotos.foundation.activity.implementation.module

import com.savvasdalkitsis.uhuruphotos.foundation.activity.implementation.holder.CurrentActivityHolder
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ActivityCreated
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityModule {

    @Binds
    @ActivityRetainedScoped
    abstract fun currentActivityHolder(holder: CurrentActivityHolder):
            com.savvasdalkitsis.uhuruphotos.foundation.activity.api.holder.CurrentActivityHolder

    @Binds
    @IntoSet
    @ActivityRetainedScoped
    abstract fun currentActivityHolderOnActivityCreated(holder: CurrentActivityHolder): ActivityCreated
}