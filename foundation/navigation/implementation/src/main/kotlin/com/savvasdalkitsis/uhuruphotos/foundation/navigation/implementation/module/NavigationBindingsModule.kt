package com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.module

import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.NavigationRouteSerializer
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.NavigationTargetBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization.AndroidBase64Transcoder
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization.Base64Transcoder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationBindingsModule {

    @Binds
    abstract fun navigator(navigator: Navigator):
        com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator

    @Binds
    abstract fun navigatorDataSerializer(navigatorDataSerializer: NavigationRouteSerializer):
        com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRouteSerializer

    @Binds
    abstract fun navigatorTargetBuilder(navigatorTargetBuilder: NavigationTargetBuilder):
        com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTargetBuilder

    @Binds
    abstract fun base64Transcoder(transcoder: AndroidBase64Transcoder): Base64Transcoder
}