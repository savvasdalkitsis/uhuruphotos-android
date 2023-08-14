package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R

@Composable
fun UpsellDialog(
    onDismiss: () -> Unit,
    onNeverAgain: (() -> Unit)? = null,
    onLogin: () -> Unit,
) {
    MultiButtonDialog(
        title = stringResource(R.string.advanced_feature_title),
        dismissButton = {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .recomposeHighlighter(),
                onClick = onDismiss,
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        onDismiss = onDismiss,
        extraButtons = if (onNeverAgain != null) listOf {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .recomposeHighlighter(),
                onClick = onNeverAgain
            ) {
                Text(stringResource(R.string.do_not_show_again))
            }
        } else emptyList(),
        confirmButton = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .recomposeHighlighter(),
                onClick = onLogin,
            ) {
                Icon(
                    painter = painterResource(drawable.ic_login),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.login))
            }
        },
    ) {
        Text(stringResource(R.string.advanced_feature_body))
    }
}