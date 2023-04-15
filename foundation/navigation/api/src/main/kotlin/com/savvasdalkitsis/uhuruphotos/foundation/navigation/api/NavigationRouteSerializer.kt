package com.savvasdalkitsis.uhuruphotos.foundation.navigation.api

import android.os.Bundle
import kotlin.reflect.KClass

interface NavigationRouteSerializer {

    fun <T : NavigationRoute> createRouteTemplateFor(route: KClass<T>): String

    fun <T : NavigationRoute> serialize(data: T): String

    fun <T : NavigationRoute> deserialize(route: KClass<T>, arguments: Bundle?): T
}