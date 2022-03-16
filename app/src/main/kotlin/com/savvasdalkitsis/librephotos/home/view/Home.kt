package com.savvasdalkitsis.librephotos.home.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Bottom
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Top
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.savvasdalkitsis.librephotos.feed.view.FeedView
import com.savvasdalkitsis.librephotos.feed.view.preview.feedStatePreview
import com.savvasdalkitsis.librephotos.home.state.HomeViewState
import com.savvasdalkitsis.librephotos.home.viewmodel.HomeViewModel
import com.savvasdalkitsis.librephotos.ui.insets.systemPadding

@Composable
fun Home(state: HomeViewState) {
    val toolbarColor = MaterialTheme.colors
        .background.copy(alpha = 0.8f)
    Scaffold(
        contentPadding = systemPadding(Bottom),
        topBar = {
            TopAppBar(
                backgroundColor = toolbarColor,
                contentPadding = systemPadding(Top),
                title = { Text(text = "LibrePhotos") }
            )
        }
    ) { contentPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            if (state.isLoading) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            } else {
                FeedView(contentPadding, state.feedState)
            }
        }
    }
}

