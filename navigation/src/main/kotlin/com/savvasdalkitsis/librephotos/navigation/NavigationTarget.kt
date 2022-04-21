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
import com.savvasdalkitsis.librephotos.log.log
import com.savvasdalkitsis.librephotos.viewmodel.EffectHandler
import com.savvasdalkitsis.librephotos.viewmodel.MVIHost
import kotlinx.coroutines.launch

fun <S : Any, E : Any, A : Any, VM> NavGraphBuilder.navigationTarget(
    name: String,
    enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    effects: EffectHandler<E>,
    initializer: (NavBackStackEntry, (A) -> Unit) -> Unit = { _, _ -> },
    createModel: @Composable () -> VM,
    content: @Composable (state: S, actions: (A) -> Unit) -> Unit,
) where VM : ViewModel, VM : MVIHost<S, E, A, *> {
    composable(
        name,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
    ) { navBackStackEntry ->
        val model = createModel()
        val scope = rememberCoroutineScope()
        val actions: (A) -> Unit = {
            scope.launch { model.action(it) }
        }

        val state by model.state.collectAsState()
        content(state, actions)

        val keyboard = LocalSoftwareKeyboardController.current
        LaunchedEffect(Unit) {
            keyboard?.hide()
            initializer(navBackStackEntry, actions)
            model.sideEffects.collect { effects(it) }
        }
    }
}