package com.savvasdalkitsis.librephotos.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.savvasdalkitsis.librephotos.ui.insets.systemPadding

@Composable
fun MainScaffolding(
    content: @Composable (PaddingValues) -> Unit,
) {
    val toolbarColor = MaterialTheme.colors
        .background.copy(alpha = 0.8f)
    Scaffold(
        contentPadding = systemPadding(WindowInsetsSides.Bottom),
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
            color = MaterialTheme.colors.background)
        {
            content(contentPadding)
        }
    }
}