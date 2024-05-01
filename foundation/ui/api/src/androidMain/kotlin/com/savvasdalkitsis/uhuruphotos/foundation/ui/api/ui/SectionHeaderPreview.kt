package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme

@Preview
@Composable
private fun SectionHeaderPreview() {
    PreviewAppTheme {
        SectionHeader(title = "Section") {
            Button(onClick = { }) {
                Text(text = "Content")
            }
        }
    }
}