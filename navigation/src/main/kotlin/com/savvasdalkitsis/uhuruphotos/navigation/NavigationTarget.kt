package com.savvasdalkitsis.uhuruphotos.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.savvasdalkitsis.uhuruphotos.ui.theme.AppTheme
import com.savvasdalkitsis.uhuruphotos.ui.theme.ThemeMode
import com.savvasdalkitsis.uhuruphotos.ui.theme.ThemeMode.*
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiverHost
import com.savvasdalkitsis.uhuruphotos.viewmodel.EffectHandler
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch

fun <S : Any, E : Any, A : Any, VM> NavGraphBuilder.navigationTarget(
    name: String,
    enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    themeMode: StateFlow<ThemeMode>,
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
        val theme by themeMode.collectAsState()
        val dark = when (theme) {
            FOLLOW_SYSTEM -> isSystemInDarkTheme()
            DARK_MODE -> true
            LIGHT_MODE -> false
        }
        AppTheme(dark) {
            content(state, action)
        }

        val keyboard = LocalSoftwareKeyboardController.current
        LaunchedEffect(Unit) {
            keyboard?.hide()
            initializer(navBackStackEntry, action)
            model.actionReceiver.effects.cancellable().collect { effects(it) }
        }
    }
}

interface NavigationTarget {
    suspend fun NavGraphBuilder.create()
}