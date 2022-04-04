package com.savvasdalkitsis.librephotos.account.view

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.savvasdalkitsis.librephotos.userbadge.view.state.UserBadgeState

@ExperimentalAnimationApi
@Composable
fun AccountOverviewPopUp(
    visible: Boolean,
    userBadgeState: UserBadgeState,
    onDismiss: () -> Unit,
    onLogoutClicked: () -> Unit,
) {
    Box {
        if (visible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background.copy(alpha = 0.3f))
            )
        }
        Popup(onDismissRequest = onDismiss) {
            AnimatedVisibility(
                modifier = Modifier
                    .padding(
                        top = 32.dp,
                        start = 16.dp,
                        end = 16.dp,
                    )
                    .align(Alignment.TopEnd),
                enter = fadeIn() + scaleIn(transformOrigin = TransformOrigin(1f, 0f)),
                exit = fadeOut() + scaleOut(transformOrigin = TransformOrigin(1f, 0f)),
                visible = visible,
            ) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .widthIn(max = 480.dp)
                        .background(MaterialTheme.colors.background),
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    AccountOverview(userBadgeState, onLogoutClicked = onLogoutClicked)
                }
            }
        }
    }
}