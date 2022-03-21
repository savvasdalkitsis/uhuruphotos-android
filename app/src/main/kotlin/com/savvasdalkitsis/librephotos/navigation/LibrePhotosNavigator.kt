package com.savvasdalkitsis.librephotos.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.savvasdalkitsis.librephotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.librephotos.server.navigation.ServerNavigationTarget
import com.savvasdalkitsis.librephotos.weblogin.navigation.WebLoginNavigationTarget
import javax.inject.Inject

class LibrePhotosNavigator @Inject constructor(
    private val navControllerProvider: NavControllerProvider,
    private val homeNavigationTarget: HomeNavigationTarget,
    private val serverNavigationTarget: ServerNavigationTarget,
    private val webLoginNavigationTarget: WebLoginNavigationTarget,
) {

    @Composable
    fun NavigationTargets(
        navController: NavHostController,
        activity: Activity,
    ) {
        navControllerProvider.navController = navController

        NavHost(navController = navController, startDestination = HomeNavigationTarget.name) {
            with(homeNavigationTarget) { create() }
            with(serverNavigationTarget) { create(activity) }
            with(webLoginNavigationTarget) { create() }
        }
    }
}
