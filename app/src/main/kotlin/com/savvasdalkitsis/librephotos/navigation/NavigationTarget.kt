package com.savvasdalkitsis.librephotos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.savvasdalkitsis.librephotos.viewmodel.MVFlowViewModel
import kotlinx.coroutines.launch


inline fun <S, A, E, reified M : MVFlowViewModel<S, A, *, E>> NavGraphBuilder.navigationTarget(
    name: String,
    crossinline effects: (E, NavHostController) -> Unit,
    crossinline viewBuilder: @Composable (S, (A) -> Unit) -> Unit,
    navController: NavHostController,
) {
    composable(name) {
        val model = hiltViewModel<M>()
        val state by model.state.observeAsState()
        val scope = rememberCoroutineScope()

        LaunchedEffect(key1 = name) {
            scope.launch {
                model.start {
                    effects(it, navController)
                }
            }
        }
        viewBuilder(state!!) { scope.launch {
            model.actions.send(it) }
        }
    }
}