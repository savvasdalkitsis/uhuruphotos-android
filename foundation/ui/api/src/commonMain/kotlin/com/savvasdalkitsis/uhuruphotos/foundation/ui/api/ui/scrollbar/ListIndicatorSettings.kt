/*
Code provided by https://blog.stackademic.com/jetpack-compose-multiplatform-scrollbar-scrolling-7c231a002ee1
 */
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scrollbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Represents the list indicators' settings, which could be enabled with specific properties or disabled.
 *
 * This sealed class allows for exhaustive when expressions in Kotlin, making it easier to handle all possible settings
 * for list indicators within a UI context. It provides two concrete implementations:
 * - [Disabled] for when indicators are not needed.
 * - [EnabledMirrored] for when indicators should be displayed, along with their customization options.
 * - [EnabledIndividualControl] for when indicators should be displayed, along with their customization options.
 */
sealed class ListIndicatorSettings {

    /**
     * Represents the state where list indicators are not shown.
     */
    data object Disabled : ListIndicatorSettings()

    /**
     * Represents the state where list indicators are enabled and can be customized but are mirrored.
     *
     * @param indicatorHeight The height of the indicator in pixels.
     * @param indicatorColor The color of the indicator.
     * @param graphicIndicator The graphic to be displayed as the indicator. This should be in the "UP" orientation.
     */
    data class EnabledMirrored(
        val indicatorHeight: Dp,
        val indicatorColor: Color,
        val graphicIndicator: @Composable (modifier: Modifier, alpha: Float) -> Unit = { _, _ -> }
    ) : ListIndicatorSettings()

    /**
     * Represents the state where list indicators are enabled and can be customized fully.
     *
     * @param upperIndicatorHeight The height of the indicator in pixels for the upper indicator.
     * @param upperIndicatorColor The color of the indicator for the upper indicator.
     * @param upperGraphicIndicator The graphic to be displayed as the indicator. This should be in the "UP" orientation.
     * @param lowerIndicatorHeight The height of the indicator in pixels for the lower indicator.
     * @param lowerIndicatorColor The color of the indicator for the lower indicator.
     * @param lowerGraphicIndicator The graphic to be displayed as the indicator. This should be in the "DOWN" orientation.
     */
    data class EnabledIndividualControl(
        val upperIndicatorHeight: Dp,
        val upperIndicatorColor: Color,
        val upperGraphicIndicator: @Composable (modifier: Modifier, alpha: Float) -> Unit = { _, _ -> },
        val lowerIndicatorHeight: Dp,
        val lowerIndicatorColor: Color,
        val lowerGraphicIndicator: @Composable (modifier: Modifier, alpha: Float) -> Unit = { _, _ -> }
    ) : ListIndicatorSettings()
}