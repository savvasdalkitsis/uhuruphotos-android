package com.savvasdalkitsis.uhuruphotos.foundation.navigation.api

import androidx.compose.runtime.compositionLocalOf

val LocalNavigationRouteSerializerProvider =
    compositionLocalOf<NavigationRouteSerializer> { throw IllegalStateException("not initialized") }