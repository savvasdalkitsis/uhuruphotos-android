package com.savvasdalkitsis.uhuruphotos.home.view

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.home.view.state.HomeState
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.ui.view.FullProgressBar

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

