package com.savvasdalkitsis.librephotos.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import com.savvasdalkitsis.librephotos.viewmodel.EffectHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.cancellable

fun <S : Any, E : Any, A : Any, VM> NavGraphBuilder.navigationTarget(
    name: String,
    enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    effects: EffectHandler<E>,
    initializer: (NavBackStackEntry, (A) -> Unit) -> Unit = { _, _ -> },
    createModel: @Composable () -> VM,
    content: @Composable (state: S, actions: (A) -> Unit) -> Unit,
) where VM : ViewModel, VM : ActionReceiverHost<S, E, A, *> {
    composable(
        name,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
    ) { navBackStackEntry ->
        val model = createModel()
        val scope = rememberCoroutineScope()
        val action: (A) -> Unit = {
            scope.launch {
                model.actionReceiver.action(it)
            }
        }

        val state by model.actionReceiver.state.collectAsState()
        content(state, action)

        val keyboard = LocalSoftwareKeyboardController.current
        LaunchedEffect(Unit) {
            keyboard?.hide()
            initializer(navBackStackEntry, action)
            model.actionReceiver.effects.cancellable().collect { effects(it) }
        }
    }
}