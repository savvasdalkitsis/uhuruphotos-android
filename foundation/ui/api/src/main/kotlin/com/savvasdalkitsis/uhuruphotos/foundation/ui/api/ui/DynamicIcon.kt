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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui

import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter

@Composable
fun DynamicIcon(
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
    when(type) {
        "drawable" -> Icon(
            modifier = modifier
                .recomposeHighlighter()
            ,
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            tint = tint ?: LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
        )
        "raw" -> {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(icon))

            val dynamicProperties = tint?.let {
                rememberLottieDynamicProperties(
                    rememberLottieDynamicProperty(
                        property = LottieProperty.COLOR,
                        value = tint.toArgb(),
                        keyPath = arrayOf(
                            "**",
                        )
                    ),
                )
            }
            LottieAnimation(
                modifier = modifier
                    .recomposeHighlighter()
                ,
                isPlaying = animateIfAvailable,
                composition = composition,
                dynamicProperties = dynamicProperties,
                iterations = LottieConstants.IterateForever,
            )
        }
    }
}