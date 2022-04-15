package com.savvasdalkitsis.librephotos.search.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.librephotos.accountoverview.view.AccountOverviewPopUp
import com.savvasdalkitsis.librephotos.home.view.HomeScaffold
import com.savvasdalkitsis.librephotos.infrastructure.extensions.blurIf
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.search.mvflow.SearchAction
import com.savvasdalkitsis.librephotos.search.mvflow.SearchAction.*
import com.savvasdalkitsis.librephotos.search.view.state.SearchState

@Composable
fun SearchPage(
    state: SearchState,
    action: (SearchAction) -> Unit,
    feedNavigationName: String,
    searchNavigationName: String,
    controllersProvider: ControllersProvider,
) {
    HomeScaffold(
        modifier = Modifier
            .blurIf(state.showAccountOverview),
        navController = controllersProvider.navController!!,
        userBadgeState = state.userBadgeState,
        feedDisplay = state.feedDisplay,
        userBadgePressed = { action(UserBadgePressed) },
        feedNavigationName = feedNavigationName,
        searchNavigationName = searchNavigationName,
    ) { contentPadding ->
        Search(
            state = state,
            action = action,
            controllersProvider = controllersProvider,
            contentPadding = contentPadding
        )
        AccountOverviewPopUp(
            visible = state.showAccountOverview,
            userBadgeState = state.userBadgeState,
            onDismiss = { action(DismissAccountOverview) },
            onLogoutClicked = { action(LogOut) },
        )
    }
}