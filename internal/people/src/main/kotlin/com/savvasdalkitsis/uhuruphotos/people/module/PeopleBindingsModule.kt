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
package com.savvasdalkitsis.uhuruphotos.people.module

import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.people.navigation.PeopleNavigationTarget
import com.savvasdalkitsis.uhuruphotos.people.usecase.PeopleUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class PeopleBindingsModule {

    @Binds
    @IntoSet
    abstract fun peopleNavigationTarget(target: PeopleNavigationTarget): NavigationTarget

    @Binds
    abstract fun peopleUseCase(peopleUseCase: PeopleUseCase):
            com.savvasdalkitsis.uhuruphotos.people.api.usecase.PeopleUseCase

}