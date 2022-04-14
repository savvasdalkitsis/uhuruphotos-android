package com.savvasdalkitsis.librephotos.home.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.librephotos.home.view.state.HomeState
import com.savvasdalkitsis.librephotos.ui.view.FullProgressBar

@Composable
fun Home(
    state: HomeState,
    feedNavigationName: String,
    searchNavigationName: String,
    controllersProvider: com.savvasdalkitsis.librephotos.app.navigation.ControllersProvider,
) {
    HomeScaffold(
        navController = controllersProvider.navController!!,
        content = {
            if (state.isLoading) {
                FullProgressBar()
            }
        },
        feedNavigationName = feedNavigationName,
        searchNavigationName = searchNavigationName,
    )
}

