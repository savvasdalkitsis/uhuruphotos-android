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

import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.IconResource
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.IconResource.*
import dev.icerock.moko.resources.FileResource
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.readTextAsState
import io.github.alexzhirkevich.compottie.LottieAnimation
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.LottieConstants
import io.github.alexzhirkevich.compottie.rememberLottieComposition

@Composable
fun DynamicIcon(
    modifier: Modifier = Modifier,
    icon: IconResource,
    contentDescription: String? = null,
    tint: Color? = null,
    animateIfAvailable: Boolean = true,
) {
    when (icon) {
        is Image -> DynamicIcon(modifier, icon.resource, contentDescription, tint, animateIfAvailable)
        is Json -> DynamicIcon(modifier, icon.resource, contentDescription, tint, animateIfAvailable)
    }
}
@Composable
fun DynamicIcon(
    modifier: Modifier = Modifier,
    icon: FileResource,
    contentDescription: String? = null,
    tint: Color? = null,
    animateIfAvailable: Boolean = true,
) {

    val iconContent by icon.readTextAsState()

    iconContent?.let {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.JsonString(
                it
            )
        )

//            val dynamicProperties = tint?.let {
//                rememberLottieDynamicProperties(
//                    rememberLottieDynamicProperty(
//                        property = LottieProperty.COLOR,
//                        value = tint.toArgb(),
//                        keyPath = arrayOf(
//                            "**",
//                        )
//                    ),
//                )
//            }
        LottieAnimation(
            modifier = modifier
                .recomposeHighlighter(),
            isPlaying = animateIfAvailable,
            composition = composition,
//                dynamicProperties = dynamicProperties,
            iterations = LottieConstants.IterateForever,
        )
    }
}

@Composable
fun DynamicIcon(
    modifier: Modifier = Modifier,
    icon: ImageResource,
    contentDescription: String? = null,
    tint: Color? = null,
    animateIfAvailable: Boolean = true,
) {
    Icon(
        modifier = modifier
            .recomposeHighlighter()
        ,
        painter = painterResource(icon),
        contentDescription = contentDescription,
        tint = tint ?: LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    )
}