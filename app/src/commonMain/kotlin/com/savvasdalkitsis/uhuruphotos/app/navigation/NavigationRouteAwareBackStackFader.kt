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

import androidx.compose.animation.core.SpringSpec
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.ui.fader.MutableUiState
import com.bumble.appyx.components.backstack.ui.fader.TargetUiState
import com.bumble.appyx.components.backstack.ui.fader.toMutableUiState
import com.bumble.appyx.interactions.ui.DefaultAnimationSpec
import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.interactions.ui.property.impl.Alpha
import com.bumble.appyx.interactions.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseVisualisation
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute

class NavigationRouteAwareBackStackFader<NavTarget : NavigationRoute>(
    uiContext: UiContext,
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseVisualisation<NavTarget, BackStackModel.State<NavTarget>, TargetUiState, MutableUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = defaultAnimationSpec,
) {
    private val visible = TargetUiState(
        alpha = Alpha.Target(1f)
    )

    private val visibleImmediately = TargetUiState(
        alpha = Alpha.Target(1f, easing = { if (it == 0f) 0f else 1f })
    )

    private val hidden = TargetUiState(
        alpha = Alpha.Target(0f)
    )

    override fun BackStackModel.State<NavTarget>.toUiTargets():
            List<MatchedTargetUiState<NavTarget, TargetUiState>> =
        listOf(
            MatchedTargetUiState(active, if (
                active.interactionTarget.animateTransitionTo &&
                active.interactionTarget.animatePopTransitionTo
            )
                visible
            else
                visibleImmediately
            )
        ) + (created + stashed + destroyed).map {
            MatchedTargetUiState(it, hidden)
        }

    override fun mutableUiStateFor(uiContext: UiContext, targetUiState: TargetUiState): MutableUiState =
        targetUiState.toMutableUiState(uiContext)
}
