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
package com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation

import android.os.Bundle
import com.google.gson.Gson
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRouteSerializer
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization.Base64String
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization.Base64Transcoder
import javax.inject.Inject
import kotlin.reflect.KClass

class NavigationRouteSerializer @Inject constructor(
    private val gson: Gson,
    private val base64: Base64Transcoder,
) : NavigationRouteSerializer {

    override fun <T : NavigationRoute> createRouteTemplateFor(route: KClass<T>): String =
        route.simpleName!! + "/{route}"

    override fun <T : NavigationRoute> serialize(data: T): String =
        createRouteTemplateFor(data::class).replace("{route}", data.encoded)

    override fun <T : NavigationRoute> deserialize(route: KClass<T>, arguments: Bundle?): T {
        val obj =  route.objectInstance
        if (obj != null) {
            return obj
        }
        return arguments!!.getString("route")!!.decodeAs(route)
    }

    private val Any.encoded get() = base64.encode(gson.toJson(this)).value
    private fun <T : Any> String.decodeAs(klass: KClass<T>): T = gson.fromJson(
        base64.decode(Base64String(this)),
        klass.java
    )

}
