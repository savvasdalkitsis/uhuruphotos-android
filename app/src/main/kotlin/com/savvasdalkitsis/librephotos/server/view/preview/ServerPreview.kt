package com.savvasdalkitsis.librephotos.server.view.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.librephotos.server.view.Server
import com.savvasdalkitsis.librephotos.server.viewmodel.state.ServerState
import com.savvasdalkitsis.librephotos.ui.theme.AppTheme

@Composable
@Preview(showBackground = true)
fun ServerPreviewLoading() {
    AppTheme {
        Server(ServerState(isLoading = true))
    }
}
@Composable
@Preview(showBackground = true)
fun ServerPreviewServerDetails() {
    AppTheme {
        Server(ServerState(
            isLoading = false,
            serverUrl = "",
        ))
    }
}