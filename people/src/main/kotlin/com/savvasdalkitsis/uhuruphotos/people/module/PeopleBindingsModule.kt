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