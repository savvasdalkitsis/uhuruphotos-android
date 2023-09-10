package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
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
        confirmButton = { LoginButton(onLogin = onLogin) },
    ) {
        Text(stringResource(R.string.advanced_feature_body))
    }
}