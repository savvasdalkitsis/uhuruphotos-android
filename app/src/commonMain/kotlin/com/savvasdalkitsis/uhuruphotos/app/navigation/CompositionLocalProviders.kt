package com.savvasdalkitsis.uhuruphotos.app.navigation

import androidx.compose.runtime.Composable

interface CompositionLocalProviders {
    @Composable
    fun Provide(
        content: @Composable () -> Unit,
    )
}