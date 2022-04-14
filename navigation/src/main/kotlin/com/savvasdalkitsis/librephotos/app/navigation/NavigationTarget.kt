package com.savvasdalkitsis.librephotos.app.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.savvasdalkitsis.librephotos.infrastructure.log.log
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import com.savvasdalkitsis.librephotos.viewmodel.EffectHandler
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.viewmodel.observe

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
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
        val actions: (A) -> Unit = {
            scope.launch {
                log("New action: $it", tag = "MVI")
                model.actionReceiver.action(it)
            }
        }

        var state by remember {
            mutableStateOf(model.initialState)
        }
        model.actionReceiver.observe(navBackStackEntry,
            state = {
                log( "New state: $it", tag = "MVI")
                state = it
            },
            sideEffect = {
                log("New side effect: $it", tag = "MVI")
                effects(it)
            }
        )
        content(state, actions)
        val keyboard = LocalSoftwareKeyboardController.current

        LaunchedEffect(Unit) {
            keyboard?.hide()
            initializer(navBackStackEntry, actions)
        }
    }
}