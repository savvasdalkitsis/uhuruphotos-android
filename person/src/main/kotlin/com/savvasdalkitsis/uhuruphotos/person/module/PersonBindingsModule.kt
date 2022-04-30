package com.savvasdalkitsis.uhuruphotos.person.module

import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.person.navigation.PersonNavigationTarget
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class PersonBindingsModule {

    @Binds
    @IntoSet
    abstract fun personNavigationTarget(target: PersonNavigationTarget): NavigationTarget

}