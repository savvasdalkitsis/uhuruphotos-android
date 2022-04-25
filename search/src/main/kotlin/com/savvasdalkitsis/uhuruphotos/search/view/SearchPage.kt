package com.savvasdalkitsis.uhuruphotos.search.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.account.view.LogOutConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.accountoverview.view.AccountOverviewPopUp
import com.savvasdalkitsis.uhuruphotos.home.view.HomeScaffold
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.blurIf
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction.*
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchState

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
        userInformationState = state.userInformationState,
        feedDisplay = state.feedDisplay,
        feedNavigationName = feedNavigationName,
        searchNavigationName = searchNavigationName,
        userBadgePressed = { action(UserBadgePressed) },
    ) { contentPadding ->
        Search(
            state = state,
            action = action,
            controllersProvider = controllersProvider,
            contentPadding = contentPadding
        )
        AccountOverviewPopUp(
            visible = state.showAccountOverview,
            userInformationState = state.userInformationState,
            onDismiss = { action(DismissAccountOverview) },
            onLogoutClicked = { action(AskToLogOut) },
            onEditServerClicked = { action(EditServer) },
            onSettingsClicked = { action(SettingsClick)}
        )
        if (state.showLogOutConfirmation) {
            LogOutConfirmationDialog(
                onDismiss = { action(DismissLogOutDialog) },
                onLogOut = { action(LogOut) },
            )
        }
    }
}