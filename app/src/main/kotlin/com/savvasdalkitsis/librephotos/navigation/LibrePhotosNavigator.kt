package com.savvasdalkitsis.librephotos.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import coil.ImageLoader
import coil.imageLoader
import com.savvasdalkitsis.librephotos.home.navigation.homeNavigationTarget
import com.savvasdalkitsis.librephotos.home.navigation.homeNavigationTargetName
import com.savvasdalkitsis.librephotos.server.navigation.serverNavigationTarget

@Composable
fun LibrePhotosNavigator(
    navController: NavHostController,
    activity: Activity,
    imageLoader: ImageLoader,
) {
    NavHost(navController = navController, startDestination = homeNavigationTargetName) {
        homeNavigationTarget(navController, imageLoader)
        serverNavigationTarget(navController, activity)
    }
}