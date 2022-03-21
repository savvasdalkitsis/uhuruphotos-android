package com.savvasdalkitsis.librephotos.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.savvasdalkitsis.librephotos.feed.view.FeedView
import com.savvasdalkitsis.librephotos.home.state.HomeState
import com.savvasdalkitsis.librephotos.main.MainScaffolding
import com.savvasdalkitsis.librephotos.ui.insets.systemPadding

@Composable
fun Home(
    state: HomeState,
    imageLoader: ImageLoader,
) {
    MainScaffolding { contentPadding ->
        if (state.isLoading && state.feedState.images.isEmpty()) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        } else {
            Box {
                FeedView(contentPadding, state.feedState, imageLoader)
                if (state.isLoading) {
                    Column(modifier = Modifier
                        .align(Alignment.BottomCenter)
                    ) {
                        Box(
                            Modifier
                                .size(36.dp)
                                .background(
                                    MaterialTheme.colors.onSecondary.copy(alpha = 0.6f),
                                    shape = CircleShape
                                )
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding() + 8.dp))
                    }
                }
            }
        }
    }
}

