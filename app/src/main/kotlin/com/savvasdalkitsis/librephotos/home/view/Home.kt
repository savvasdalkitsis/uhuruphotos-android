package com.savvasdalkitsis.librephotos.home.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.librephotos.home.view.state.HomeState
import com.savvasdalkitsis.librephotos.ui.view.FullProgressBar

@Composable
fun Home(
    state: HomeState,
    controllersProvider: com.savvasdalkitsis.librephotos.navigation.ControllersProvider,
) {
    HomeScaffold(
        navController = controllersProvider.navController!!,
        content = {
            if (state.isLoading) {
                FullProgressBar()
            }
        },
    )
}

