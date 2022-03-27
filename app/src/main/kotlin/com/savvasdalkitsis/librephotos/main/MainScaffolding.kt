package com.savvasdalkitsis.librephotos.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.BottomNavigation
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.savvasdalkitsis.librephotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.librephotos.navigation.BottomNavItem
import com.savvasdalkitsis.librephotos.search.navigation.SearchNavigationTarget
import com.savvasdalkitsis.librephotos.views.CommonScaffolding

@Composable
fun MainScaffolding(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit,
) {
    CommonScaffolding(
        bottomBar = {
            BottomNavigation(
                backgroundColor = MaterialTheme.colors.primarySurface.copy(alpha = 0.8f)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                BottomNavItem(
                    currentDestination, navController,
                    label = "Home",
                    routeName = HomeNavigationTarget.name,
                    Icons.Filled.Home,
                )
                BottomNavItem(
                    currentDestination, navController,
                    label = "Search",
                    routeName = SearchNavigationTarget.name,
                    Icons.Filled.Search,
                )
            }
        }
    ) { contentPadding ->
        content(contentPadding)
    }
}