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
package com.savvasdalkitsis.uhuruphotos.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.node
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.LocalBackStack
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTargetRegistry

class NavigationTree(
    nodeContext: NodeContext,
    private val backStack: BackStack<NavigationRoute>,
) : Node<NavigationRoute>(
    nodeContext = nodeContext,
    appyxComponent = backStack,
) {
    @Composable
    override fun Content(modifier: Modifier) {
        AppyxNavigationContainer(
            appyxComponent = backStack,
        )
    }

    override fun buildChildNode(navTarget: NavigationRoute, nodeContext: NodeContext): Node<*> =
        node(nodeContext) {
            CompositionLocalProvider(
                LocalBackStack provides { backStack }
            ) {
                NavigationTargetRegistry.registry[navTarget::class]!!.NavigationRootView(navTarget)
            }
        }
}