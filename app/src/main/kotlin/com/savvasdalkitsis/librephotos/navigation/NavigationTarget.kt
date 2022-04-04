package com.savvasdalkitsis.librephotos.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.savvasdalkitsis.librephotos.log.log
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import com.savvasdalkitsis.librephotos.viewmodel.EffectHandler
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.viewmodel.observe

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
inline fun <S : Any, E : Any, A : Any, reified VM> NavGraphBuilder.navigationTarget(
    name: String,
    crossinline effects: EffectHandler<E>,
    crossinline initializer: (NavBackStackEntry, (A) -> Unit) -> Unit = { _, _ -> },
    crossinline content: @Composable (state: S, actions: (A) -> Unit) -> Unit,
) where VM : ViewModel, VM : ActionReceiverHost<S, E, A, *> {
    composable(name) { navBackStackEntry ->
        val model = hiltViewModel<VM>()
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