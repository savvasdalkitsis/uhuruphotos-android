package com.savvasdalkitsis.librephotos.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.savvasdalkitsis.librephotos.viewmodel.MVFlowViewModel
import kotlinx.coroutines.launch

inline fun <S, A, E, reified M : MVFlowViewModel<S, A, *, E>> NavGraphBuilder.navigationTarget(
    name: String,
    crossinline effects: (E, ControllersProvider) -> Unit,
    crossinline viewBuilder: @Composable (S, (A) -> Unit) -> Unit,
    crossinline initializer: (NavBackStackEntry, (A) -> Unit) -> Unit = { _, _ -> },
    controllersProvider: ControllersProvider,
) {
    composable(name) { navBackStackEntry ->
        val model = hiltViewModel<M>()
        val state by model.state.observeAsState()
        val scope = rememberCoroutineScope()

        val actions: (A) -> Unit = {
            scope.launch {
                model.actions.send(it)
            }
        }
        initializer(navBackStackEntry, actions)

        LaunchedEffect(key1 = name) {
            scope.launch {
                model.start {
                    effects(it, controllersProvider)
                }
            }
        }
        viewBuilder(state!!, actions)
    }
}



@Composable
fun RowScope.BottomNavItem(
    currentDestination: NavDestination?,
    navController: NavHostController,
    label: String,
    routeName: String,
    icon: ImageVector
) {

    BottomNavigationItem(
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        selected = currentDestination?.hierarchy?.any { it.route == routeName } == true,
        onClick = {
            if (currentDestination?.route == routeName)
                return@BottomNavigationItem

            navController.navigate(routeName) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}