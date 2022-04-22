package com.savvasdalkitsis.librephotos.accountoverview.view

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.savvasdalkitsis.librephotos.userbadge.api.view.state.UserInformationState

@Composable
fun AccountOverviewPopUp(
    visible: Boolean,
    userInformationState: UserInformationState,
    onDismiss: () -> Unit,
    onLogoutClicked: () -> Unit,
    onEditServerClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
) {
    Box {
        if (visible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background.copy(alpha = 0.3f))
                    .clickable { onDismiss() }
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
                    Row {
                        Spacer(modifier = Modifier.fillMaxWidth().weight(1f))
                        IconButton(
                            modifier = Modifier.align(Alignment.Top),
                            onClick = onDismiss
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "close")
                        }
                    }
                    AccountOverview(
                        userInformationState,
                        onLogoutClicked,
                        onEditServerClicked,
                        onSettingsClicked,
                    )
                }
            }
        }
    }
}