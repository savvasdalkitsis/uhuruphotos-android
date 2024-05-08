/*
Code provided by https://blog.stackademic.com/jetpack-compose-multiplatform-scrollbar-scrolling-7c231a002ee1
 */
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scrollbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Shows a scroll arrow.
 *
 * @param upIndication Whether the arrow should be displayed point up; True for pointing up.
 */
@Composable
internal fun DisplayIndicator(
    modifier: Modifier = Modifier,
    upIndication: Boolean,
    indicatorHeight: Dp,
    indicatorColor: Color,
    alpha: Float,
    graphicIndicator: @Composable (modifier: Modifier, alpha: Float) -> Unit = { _, _ -> },
    graphicModifier: Modifier = Modifier
) {
    val ratio = .5f
    Box(
        modifier = modifier
            .fillMaxWidth(1f)
            .height(IntrinsicSize.Max),
        contentAlignment = if (upIndication) Alignment.TopCenter else Alignment.BottomCenter,
    ) {
        Box(
            Modifier.height(indicatorHeight).fillMaxWidth().background(
                if (upIndication) {
                    Brush.verticalGradient(
                        listOf(
                            indicatorColor.copy(alpha = alpha),
                            indicatorColor.copy(alpha = alpha * .75f),
                            Color.Transparent
                        ),
                        startY = indicatorHeight.value * (ratio)
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            indicatorColor.copy(alpha = alpha * .75f),
                            indicatorColor.copy(alpha = alpha)
                        ),
                        startY = indicatorHeight.value * ratio
                    )
                }
            )
        )
        graphicIndicator.invoke(graphicModifier, alpha)
    }
}