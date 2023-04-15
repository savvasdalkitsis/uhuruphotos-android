package com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.ServerAction

@Composable
internal fun Feedback(
    state: ServerState,
    action: (ServerAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        EnableLoggingCheckbox(state, action)
        SendFeedbackButton(action)
    }
}