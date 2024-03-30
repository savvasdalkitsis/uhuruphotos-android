/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.app.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.Pop
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute

@Suppress("TransitionPropertiesLabel")
class NavigationRouteAwareBackStackFader(
    private val transitionSpec: TransitionSpec<BackStack.State, Float> = { spring() }
) : ModifierTransitionHandler<NavigationRoute, BackStack.State>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<BackStack.State>,
        descriptor: TransitionDescriptor<NavigationRoute, BackStack.State>
    ): Modifier = modifier.composed {
        val animate = if (descriptor.operation is Pop<NavigationRoute>) {
            descriptor.element.animatePopTransitionTo
        } else {
            descriptor.element.animateTransitionTo
        }
        if (animate) {
            val alpha = transition.animateFloat(
                transitionSpec = transitionSpec,
                targetValueByState = {
                    when (it) {
                        BackStack.State.ACTIVE -> 1f
                        else -> 0f
                    }
                })

            alpha(alpha.value)
        } else {
            this
        }
    }
}

@Composable
fun rememberNavigationRouteAwareBackstackFader(
    transitionSpec: TransitionSpec<BackStack.State, Float> = { spring() }
): ModifierTransitionHandler<NavigationRoute, BackStack.State> = remember {
    NavigationRouteAwareBackStackFader(transitionSpec = transitionSpec)
}
