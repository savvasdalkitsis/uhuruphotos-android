/*
Copyright 2023 Savvas Dalkitsis

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
