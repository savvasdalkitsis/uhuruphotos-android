package com.savvasdalkitsis.librephotos.server.view.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.librephotos.server.view.Server
import com.savvasdalkitsis.librephotos.server.view.ServerState
import com.savvasdalkitsis.librephotos.ui.theme.AppTheme

@Composable
@Preview(showBackground = true)
fun ServerPreviewLoading() {
    AppTheme {
        Server(ServerState.Loading)
    }
}
@Composable
@Preview(showBackground = true)
fun ServerPreviewServerDetails() {
    AppTheme {
        Server(ServerState.ServerUrl("", isUrlValid = false, allowSaveUrl = false))
    }
}