package com.savvasdalkitsis.librephotos.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.savvasdalkitsis.librephotos.log.log
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import com.savvasdalkitsis.librephotos.viewmodel.EffectHandler
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.viewmodel.observe

inline fun <S : Any, E : Any, A : Any, reified VM> NavGraphBuilder.navigationTarget(
    name: String,
    crossinline effects: EffectHandler<E>,
    crossinline initializer: (NavBackStackEntry, (A) -> Unit) -> Unit = { _, _ -> },
    crossinline content: @Composable (state: S, actions: (A) -> Unit) -> Unit,
) where VM : ViewModel, VM : ActionReceiverHost<S, E, A, *> {
    composable(name) { navBackStackEntry ->
        val model = hiltViewModel<VM>()
        val scope = rememberCoroutineScope()
        val actions: (A) -> Unit = remember {
            {
                scope.launch {
                    log("New action: $it", tag = "MVI")
                    model.actionReceiver.action(it)
                }
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

        LaunchedEffect(Unit) {
            initializer(navBackStackEntry, actions)
        }
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