package com.savvasdalkitsis.librephotos.userbadge.view

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.savvasdalkitsis.librephotos.R
import com.savvasdalkitsis.librephotos.ui.theme.CustomColors
import com.savvasdalkitsis.librephotos.userbadge.view.state.SyncState.*
import com.savvasdalkitsis.librephotos.userbadge.view.state.UserBadgeState
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.coil.LocalCoilImageLoader
import com.skydoves.landscapist.rememberDrawablePainter

@Composable
fun UserBadge(
    state: UserBadgeState,
    imageLoader: ImageLoader? = null
) {
    val size = 38.dp
    val backgroundColor = when (state.syncState) {
        BAD -> CustomColors.syncError
        GOOD -> CustomColors.syncSuccess
        IN_PROGRESS -> MaterialTheme.colors.background
    }
    Box(modifier = Modifier
        .clip(CircleShape)
        .background(backgroundColor)
        .size(size)
    ) {
        if (state.syncState == IN_PROGRESS) {
            CircularProgressIndicator(modifier = Modifier.size(size))
        }

        when {
            !state.avatarUrl.isNullOrEmpty() -> CoilImage(
                modifier = Modifier
                    .size(size - 6.dp)
                    .clip(CircleShape)
                    .align(Alignment.Center),
                imageLoader = { imageLoader ?: LocalCoilImageLoader.current!! },
                imageModel = state.avatarUrl,
                contentScale = ContentScale.FillBounds,
                placeHolder = rememberDrawablePainter(ColorDrawable(backgroundColor.toArgb())),
            )
            state.initials.isNotEmpty() -> Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = state.initials
            )
            else -> Icon(
                modifier = Modifier
                    .align(Alignment.Center),
                painter = painterResource(R.drawable.ic_person),
                contentDescription = "profileIcon"
            )
        }
    }
}