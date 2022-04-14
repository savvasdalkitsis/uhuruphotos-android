package com.savvasdalkitsis.librephotos.account.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.librephotos.icons.R
import com.savvasdalkitsis.librephotos.userbadge.view.UserBadge
import com.savvasdalkitsis.librephotos.userbadge.view.state.UserBadgeState

@Composable
fun AccountOverview(
    userBadgeState: UserBadgeState,
    onLogoutClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .defaultMinSize(minHeight = 120.dp)
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row {
            UserBadge(
                state = userBadgeState,
                size = 48.dp
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