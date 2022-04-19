package com.savvasdalkitsis.librephotos.accountoverview.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvasdalkitsis.librephotos.icons.R
import com.savvasdalkitsis.librephotos.ui.view.ActionIcon
import com.savvasdalkitsis.librephotos.userbadge.view.UserBadge
import com.savvasdalkitsis.librephotos.userbadge.view.state.UserInformationState

@Composable
fun AccountOverview(
    userInformationState: UserInformationState,
    onLogoutClicked: () -> Unit,
    onEditServerClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .defaultMinSize(minHeight = 120.dp)
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserBadge(
                state = userInformationState,
                size = 48.dp
            )
            Column {
                Text(
                    text = userInformationState.userFullName,
                    style = TextStyle.Default.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = userInformationState.serverUrl,
                    style = MaterialTheme.typography.caption.copy(color = Color.Gray),
                )
            }
            ActionIcon(
                onClick = onEditServerClicked,
                icon = R.drawable.ic_edit,
            )
        }
        Button(
            modifier = Modifier.align(End),
            onClick = onLogoutClicked,
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Log out")
        }
    }
}