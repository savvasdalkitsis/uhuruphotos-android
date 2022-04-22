package com.savvasdalkitsis.librephotos.userbadge.api.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.librephotos.icons.R
import com.savvasdalkitsis.librephotos.image.api.view.Image
import com.savvasdalkitsis.librephotos.ui.theme.CustomColors
import com.savvasdalkitsis.librephotos.userbadge.api.view.state.SyncState.*
import com.savvasdalkitsis.librephotos.userbadge.api.view.state.UserInformationState

@Composable
fun UserBadge(
    state: UserInformationState,
    userBadgePressed: (() -> Unit)? = null,
    size: Dp = 38.dp,
) {
    val backgroundColor = when (state.syncState) {
        BAD -> CustomColors.syncError
        GOOD -> CustomColors.syncSuccess
        IN_PROGRESS -> MaterialTheme.colors.background
    }
    Box(modifier = Modifier
        .let {
            when(userBadgePressed) {
                null -> it
                else -> it.clickable { userBadgePressed() }
            }
        }
        .clip(CircleShape)
        .background(backgroundColor)
        .size(size)
    ) {
        if (state.syncState == IN_PROGRESS) {
            CircularProgressIndicator(modifier = Modifier.size(size))
        }

        when {
            !state.avatarUrl.isNullOrEmpty() -> Image(
                modifier = Modifier
                    .size(size - 6.dp)
                    .clip(CircleShape)
                    .align(Alignment.Center),
                url = state.avatarUrl,
                contentScale = ContentScale.FillBounds,
                placeholder = ColorPainter(backgroundColor),
                contentDescription = "profileImage"
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