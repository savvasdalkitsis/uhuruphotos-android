package com.savvasdalkitsis.librephotos.views

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.savvasdalkitsis.librephotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.librephotos.navigation.BottomNavItem
import com.savvasdalkitsis.librephotos.search.navigation.SearchNavigationTarget
import com.savvasdalkitsis.librephotos.ui.insets.systemPadding

@Composable
fun CommonScaffolding(
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val toolbarColor = MaterialTheme.colors
        .background.copy(alpha = 0.8f)
    Scaffold(
        contentPadding = systemPadding(WindowInsetsSides.Bottom),
        bottomBar = { bottomBar() },
        topBar = {
            TopAppBar(
                backgroundColor = toolbarColor,
                contentPadding = systemPadding(WindowInsetsSides.Top),
                title = { Text(text = "LibrePhotos") }
            )
        }
    ) { contentPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        )
        {
            content(contentPadding)
        }
    }
}