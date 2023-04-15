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