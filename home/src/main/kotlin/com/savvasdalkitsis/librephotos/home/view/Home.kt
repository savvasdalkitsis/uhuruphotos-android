package com.savvasdalkitsis.librephotos.home.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.librephotos.home.view.state.HomeState
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.ui.view.FullProgressBar

@Composable
fun Home(
    state: HomeState,
    feedNavigationName: String,
    searchNavigationName: String,
    controllersProvider: ControllersProvider,
) {
    HomeScaffold(
        navController = controllersProvider.navController!!,
        feedNavigationName = feedNavigationName,
        searchNavigationName = searchNavigationName,
    ) {
        if (state.isLoading) {
            FullProgressBar()
        }
    }
}

