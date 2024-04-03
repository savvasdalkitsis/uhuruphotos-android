/*
Copyright 2023 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.hdr.setHDR
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalFullImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import me.saket.telephoto.zoomable.ZoomableImageState
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage

@Composable
fun FullSizeImage(
    modifier: Modifier = Modifier,
    lowResUrl: String?,
    fullResUrl: String?,
    onFullResImageLoaded: () -> Unit = {},
    contentScale: ContentScale,
    onClick: () -> Unit,
    contentDescription: String?,
    zoomableState: ZoomableImageState,
) {
    val context = LocalContext.current

    var thumb by remember {
        mutableStateOf<Drawable?>(null)
    }
    val imageLoader = LocalThumbnailImageLoader.current
    val request = lowResUrl?.toRequest(
        onSuccess = { result ->
            thumb = result.drawable
        },
    )
    LaunchedEffect(lowResUrl) {
        if (request != null) {
            imageLoader.execute(request)
        }
    }

    ZoomableAsyncImage(
        modifier = modifier
            .fillMaxSize(),
        imageLoader = LocalFullImageLoader.current,
        state = zoomableState,
        onClick = { onClick() },
        model = ImageRequest.Builder(context)
            .data(fullResUrl)
            .placeholder(thumb)
            .listener(remember {
                object : ImageRequest.Listener {
                    override fun onSuccess(request: ImageRequest, result: SuccessResult)  {
                        context.setHDR(result.isHDR)
                        onFullResImageLoaded()
                    }
                }
            })
            .build(),
        contentScale = contentScale,
        contentDescription = contentDescription,
    )
}

private val SuccessResult.isHDR: Boolean
    get() = when {
        SDK_INT >= UPSIDE_DOWN_CAKE -> (drawable as? BitmapDrawable)?.bitmap?.hasGainmap() == true
        else -> false
    }