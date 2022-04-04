package com.savvasdalkitsis.librephotos.main.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ui.Scaffold
import com.savvasdalkitsis.librephotos.R
import com.savvasdalkitsis.librephotos.ui.insets.systemPadding

@Composable
fun MainScaffold(
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {},
    actionBarContent: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val toolbarColor = MaterialTheme.colors
        .background.copy(alpha = 0.8f)
    Scaffold(
        modifier = modifier,
        contentPadding = systemPadding(WindowInsetsSides.Bottom),
        bottomBar = { bottomBar() },
        topBar = {
            TopAppBar(
                backgroundColor = toolbarColor,
                contentPadding = systemPadding(WindowInsetsSides.Top),
                content = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Spacer(Modifier.width(12.dp))
                            ProvideTextStyle(value = MaterialTheme.typography.h6) {
                                CompositionLocalProvider(
                                    LocalContentAlpha provides ContentAlpha.high,
                                    content = {
                                        Text(text = "LibrePhotos")
                                    }
                                )
                            }
                        }
                        Box(modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(
                                start = 2.dp,
                                top = 2.dp,
                                bottom = 2.dp,
                                end = 12.dp,
                            )
                        ) {
                            actionBarContent()
                        }
                    }
                }
            )
        }
    ) { contentPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            content(contentPadding)
        }
    }
}