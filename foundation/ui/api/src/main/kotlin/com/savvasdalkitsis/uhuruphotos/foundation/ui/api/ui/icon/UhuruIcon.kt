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
@file:OptIn(ExperimentalResourceApi::class)

package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.animation.AnimationResource
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeVariant
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_delete

@Composable
fun UhuruIcon(
    modifier: Modifier = Modifier,
    icon: AnimationResource,
    contentDescription: String? = null,
    tint: Color? = null,
    animateIfAvailable: Boolean = true,
) {
    val composition by io.github.alexzhirkevich.compottie.rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/${icon.name}.json").decodeToString()
        )
    }
    Image(
        modifier = modifier
            .recomposeHighlighter()
        ,
        painter = rememberLottiePainter(
            composition = composition,
            dynamicProperties = autoTint(),
            isPlaying = animateIfAvailable,
            iterations = Compottie.IterateForever,
        ),
        contentDescription = contentDescription,
    )
}

@Composable
fun UhuruIcon(
    modifier: Modifier = Modifier,
    icon: DrawableResource,
    contentDescription: String? = null,
    tint: Color? = null,
    animateIfAvailable: Boolean = true,
) {
    UhuruIcon(modifier, painterResource(icon), contentDescription, tint, animateIfAvailable)
}

@Composable
fun UhuruIcon(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String? = null,
    tint: Color? = null,
    animateIfAvailable: Boolean = true,
) {
    val resolvedTint = tint ?: MaterialTheme.colorScheme.onBackground
    Icon(
        modifier = modifier
            .recomposeHighlighter()
        ,
        painter = painter,
        contentDescription = contentDescription,
        tint = resolvedTint,
    )
}

private data class PreviewData(
    val icon: Either<DrawableResource, AnimationResource>,
    val themeMode: ThemeMode,
    val themeVariant: ThemeVariant,
)

private class PreviewDataProvider : PreviewParameterProvider<PreviewData> {
    override val values: Sequence<PreviewData> =
        ThemeMode.entries.flatMap { themeMode ->
            ThemeVariant.entries.flatMap { themeVariant ->
                listOf(
                    PreviewData(Either.Left(drawable.ic_delete), themeMode, themeVariant),
                    PreviewData(Either.Right(AnimationResource.animation_empty), themeMode, themeVariant),
                    PreviewData(Either.Right(AnimationResource.ic_animated_cloud_upload), themeMode, themeVariant),
                    PreviewData(Either.Right(AnimationResource.ic_animated_cloud_download), themeMode, themeVariant),
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
        when (val icon = data.icon) {
            is Either.Left<DrawableResource, *> -> UhuruIcon(modifier = Modifier.size(128.dp), icon = icon.value)
            is Either.Right<*, AnimationResource> -> UhuruIcon(modifier = Modifier.size(128.dp), icon = icon.value)
        }

    }
}