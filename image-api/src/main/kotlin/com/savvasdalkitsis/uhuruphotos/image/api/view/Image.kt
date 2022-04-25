package com.savvasdalkitsis.uhuruphotos.image.api.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun Image(
    modifier: Modifier = Modifier,
    url: String?,
    contentScale: ContentScale,
    placeholder: Painter? = null,
    contentDescription: String?,
) {
    AsyncImage(
        modifier = modifier,
        model = url,
        contentScale = contentScale,
        placeholder = placeholder,
        contentDescription = contentDescription,
    )
}

@Composable
fun Image(
    modifier: Modifier = Modifier,
    lowResUrl: String?,
    fullResUrl: String?,
    onFullResImageLoaded: () -> Unit = {},
    contentScale: ContentScale,
    placeholder: Painter? = null,
    contentDescription: String?,
) {
    var showLowRes = remember { true }

    Box(modifier = modifier) {
        if (showLowRes) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                url = lowResUrl,
                contentScale = contentScale,
                placeholder = placeholder,
                contentDescription = null,
            )
        }
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            model = ImageRequest.Builder(LocalContext.current)
                .data(fullResUrl)
                .size(Size.ORIGINAL)
                .listener(onSuccess = { _, _ ->
                    showLowRes = false
                    onFullResImageLoaded()
                })
                .build(),
            contentScale = contentScale,
            contentDescription = contentDescription,
        )
    }

}