/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon

import android.R.drawable.ic_delete
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.raw.ic_animated_cloud_download
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.raw.ic_animated_cloud_upload
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeVariant
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.R.raw.animation_empty

@Composable
fun UhuruIcon(
    modifier: Modifier = Modifier,
    icon: Int,
    contentDescription: String? = null,
    tint: Color? = null,
    animateIfAvailable: Boolean = true,
) {
    val resources = LocalContext.current.resources
    val type = remember(icon) {
        resources.getResourceTypeName(icon)
    }
    val resolvedTint = tint ?: MaterialTheme.colorScheme.onBackground
    when(type) {
        "drawable" -> {
            Icon(
                modifier = modifier
                    .recomposeHighlighter()
                ,
                painter = painterResource(id = icon),
                contentDescription = contentDescription,
                tint = resolvedTint,
            )
        }
        "raw" -> {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(icon))
            LottieAnimation(
                modifier = modifier
                    .recomposeHighlighter()
                ,
                isPlaying = animateIfAvailable,
                composition = composition,
                dynamicProperties = autoTint(),
                iterations = LottieConstants.IterateForever,
            )
        }
    }
}

private data class PreviewData(
    val icon: Int,
    val themeMode: ThemeMode,
    val themeVariant: ThemeVariant,
)

private class PreviewDataProvider : PreviewParameterProvider<PreviewData> {
    override val values: Sequence<PreviewData> =
        ThemeMode.entries.flatMap { themeMode ->
            ThemeVariant.entries.flatMap { themeVariant ->
                listOf(
                    PreviewData(ic_delete, themeMode, themeVariant),
                    PreviewData(animation_empty, themeMode, themeVariant),
                    PreviewData(ic_animated_cloud_upload, themeMode, themeVariant),
                    PreviewData(ic_animated_cloud_download, themeMode, themeVariant),
                )
             }
        }.asSequence()
}

@Preview
@Composable
private fun UhuruIconPreview(@PreviewParameter(PreviewDataProvider::class) data: PreviewData) {
    PreviewAppTheme(
        themeMode = data.themeMode,
        theme = data.themeVariant,
    ) {
        UhuruIcon(modifier = Modifier.size(128.dp), icon = data.icon)
    }
}