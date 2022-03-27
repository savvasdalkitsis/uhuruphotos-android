package com.savvasdalkitsis.librephotos.feed.view.preview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.librephotos.feed.view.FeedState
import com.savvasdalkitsis.librephotos.feed.view.FeedView
import com.savvasdalkitsis.librephotos.ui.theme.AppTheme

val feedStatePreview = FeedState()

@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    AppTheme {
        FeedView(PaddingValues(0.dp), feedStatePreview)
    }
}