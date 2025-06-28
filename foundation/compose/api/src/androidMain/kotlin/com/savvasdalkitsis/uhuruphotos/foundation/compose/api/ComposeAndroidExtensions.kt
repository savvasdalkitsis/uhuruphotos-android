package com.savvasdalkitsis.uhuruphotos.foundation.compose.api

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedElement(scope: SharedTransitionScope, id: String): Modifier {
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current
    return with(scope) {
        sharedElement(
            sharedContentState = rememberSharedContentState(id),
            animatedVisibilityScope = animatedVisibilityScope,
        )
    }
}