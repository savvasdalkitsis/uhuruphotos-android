package com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
context(scope: SharedTransitionScope)
fun Modifier.sharedElement(id: SharedElementId): Modifier {
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current
    return with(scope) {
        sharedElement(
            sharedContentState = rememberSharedContentState(id.value),
            animatedVisibilityScope = animatedVisibilityScope,
        )
    }
}