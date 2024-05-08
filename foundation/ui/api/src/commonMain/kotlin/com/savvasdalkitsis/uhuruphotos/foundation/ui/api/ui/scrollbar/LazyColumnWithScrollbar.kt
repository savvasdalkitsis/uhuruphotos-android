/*
Code provided by https://blog.stackademic.com/jetpack-compose-multiplatform-scrollbar-scrolling-7c231a002ee1
 */
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scrollbar

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import kotlin.time.*

/**
 * A custom scrollbar implementation for `LazyColumn` in Jetpack Compose, supporting various customization options.
 *
 * This Composable function adds a customizable scrollbar to a LazyColumn, with features like dynamic visibility,
 * drag to scroll, customizable thumb size, and appearance.
 *
 * @param listState The state object of the LazyList this scrollbar is connected to. This is used to synchronize
 * the scrollbar's position and size with the list's current scroll state.
 * @param modifier [Modifier] to apply to this scrollbar.
 * @param rightSide Determines if the scrollbar is to be displayed on the right side. Default is `true`.
 * @param alwaysShowScrollBar When `true`, the scrollbar remains visible at all times. Otherwise, it shows only
 * during scrolling.
 * @param thickness The thickness of the scrollbar.
 * @param padding Padding around the scrollbar.
 * @param thumbMinHeight The minimum height of the thumb within the scrollbar to ensure it's always touch-friendly.
 * @param thumbColor The color of the scrollbar thumb.
 * @param thumbSelectedColor The color of the scrollbar thumb when it is being dragged.
 * @param thumbShape The shape of the scrollbar thumb.
 * @param selectionMode Specifies how the thumb can be selected or dragged. Can be one of [ScrollbarSelectionMode].
 * @param selectionActionable Determines when the scrollbar is actionable, controlled by [ScrollbarSelectionActionable].
 * @param hideDelay The delay duration before the scrollbar fades out after interaction. Relevant when
 * [alwaysShowScrollBar] is `false`.
 * @param indicatorContent Optional Composable content displayed alongside the thumb, allowing for custom indicators
 * at the thumb's position.
 */
@Composable
fun LazyColumnWithScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    rightSide: Boolean = true,
    alwaysShowScrollBar: Boolean = false,
    thickness: Dp = 6.dp,
    padding: Dp = 8.dp,
    thumbMinHeight: Float = 0.1f,
    thumbColor: Color = MaterialTheme.colors.primary,
    thumbSelectedColor: Color = Color(0xFF5281CA),
    thumbShape: Shape = CircleShape,
    selectionMode: ScrollbarSelectionMode = ScrollbarSelectionMode.Thumb,
    selectionActionable: ScrollbarSelectionActionable = ScrollbarSelectionActionable.Always,
    hideDelay: Duration = 400.toDuration(DurationUnit.MILLISECONDS),
    showItemIndicator: ListIndicatorSettings = ListIndicatorSettings.EnabledMirrored(
        100.dp,
        MaterialTheme.colors.surface
    ),
    enabled: Boolean = true,
    indicatorContent: (@Composable (index: Int, isThumbSelected: Boolean) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if ((!enabled)) {
        content()
    } else {
        Box(modifier = modifier) {
            val visibilityState = remember {
                derivedStateOf {
                    calculateVisibilityStates(listState, showItemIndicator)
                }
            }

            // Use animateFloatAsState to smoothly transition the alpha value
            val alphaAbove: Float by animateFloatAsState(
                if (visibilityState.value.first != VisibilityState.COMPLETELY_VISIBLE) 1f else 0f,
                animationSpec = tween(250)
            )
            val alphaBelow: Float by animateFloatAsState(
                if (visibilityState.value.second != VisibilityState.COMPLETELY_VISIBLE) 1f else 0f,
                animationSpec = tween(250)
            )

            val heightAbove: Float by animateFloatAsState(
                if (visibilityState.value.first == VisibilityState.NOT_VISIBLE) .8f else .25f,
                animationSpec = tween(1000)
            )

            val heightBelow: Float by animateFloatAsState(
                if (visibilityState.value.second == VisibilityState.NOT_VISIBLE) 1f else .25f,
                animationSpec = tween(1000)
            )

            content()
            when(showItemIndicator){
                ListIndicatorSettings.Disabled -> {
                    // Do nothing
                }
                is ListIndicatorSettings.EnabledIndividualControl -> {
                    DisplayIndicator(
                        modifier = Modifier.align(Alignment.TopCenter).focusable(false),
                        upIndication = true,
                        indicatorHeight = showItemIndicator.upperIndicatorHeight * heightAbove,
                        indicatorColor = showItemIndicator.upperIndicatorColor,
                        alpha = alphaAbove,
                        graphicIndicator = showItemIndicator.upperGraphicIndicator
                    )

                    DisplayIndicator(
                        modifier = Modifier.align(Alignment.BottomCenter).focusable(false),
                        upIndication = false,
                        indicatorHeight = showItemIndicator.lowerIndicatorHeight * heightBelow,
                        indicatorColor = showItemIndicator.lowerIndicatorColor.copy(alpha = alphaBelow),
                        alpha = alphaBelow,
                        graphicIndicator = showItemIndicator.lowerGraphicIndicator
                    )
                }
                is ListIndicatorSettings.EnabledMirrored -> {
                    DisplayIndicator(
                        modifier = Modifier.align(Alignment.TopCenter).focusable(false),
                        upIndication = true,
                        indicatorHeight = showItemIndicator.indicatorHeight * heightAbove,
                        indicatorColor = showItemIndicator.indicatorColor,
                        alpha = alphaAbove,
                        graphicIndicator = showItemIndicator.graphicIndicator
                    )

                    DisplayIndicator(
                        modifier = Modifier.align(Alignment.BottomCenter).focusable(false),
                        upIndication = false,
                        indicatorHeight = showItemIndicator.indicatorHeight * heightBelow,
                        indicatorColor = showItemIndicator.indicatorColor.copy(alpha = alphaBelow),
                        alpha = alphaBelow,
                        graphicIndicator = showItemIndicator.graphicIndicator,
                        graphicModifier = Modifier.rotate(180f)
                    )
                }
            }

            LazyColumnScrollbar(
                listState = listState,
                modifier = Modifier,
                rightSide = rightSide,
                alwaysShowScrollBar = alwaysShowScrollBar,
                thickness = thickness,
                padding = padding,
                thumbMinHeight = thumbMinHeight,
                thumbColor = thumbColor,
                thumbSelectedColor = thumbSelectedColor,
                selectionActionable = selectionActionable,
                hideDelay = hideDelay,
                thumbShape = thumbShape,
                selectionMode = selectionMode,
                indicatorContent = indicatorContent,
            )
        }
    }
}


private fun calculateVisibilityStates(
    listState: LazyListState,
    showItemIndicator: ListIndicatorSettings
): Pair<VisibilityState, VisibilityState> {
    val layoutInfo = listState.layoutInfo
    val totalItemCount = layoutInfo.totalItemsCount
    val visibleItems = layoutInfo.visibleItemsInfo
    val firstVisibleItemIndex = listState.firstVisibleItemIndex
    val firstItemVisibleOffset = listState.firstVisibleItemScrollOffset
    val viewportSize = layoutInfo.viewportSize.height

    if (layoutInfo.totalItemsCount == 0) {
        return Pair(VisibilityState.NOT_VISIBLE, VisibilityState.NOT_VISIBLE)
    }

    if (showItemIndicator is ListIndicatorSettings.Disabled) {
        return Pair(VisibilityState.COMPLETELY_VISIBLE, VisibilityState.COMPLETELY_VISIBLE)
    }

    // Calculate visibility for content above
    val contentAboveState = when {
        !layoutInfo.reverseLayout -> {
            if (firstVisibleItemIndex == 0 && firstItemVisibleOffset == 0) VisibilityState.COMPLETELY_VISIBLE
            else if (visibleItems.none { it.index == 0 }) VisibilityState.NOT_VISIBLE
            else VisibilityState.PARTIALLY_VISIBLE
        }
        else -> {
            determineVisibilityState(visibleItems, totalItemCount, viewportSize)
        }
    }

    // Calculate visibility for content below
    val contentBelowState = when {
        !layoutInfo.reverseLayout -> {
            determineVisibilityState(visibleItems, totalItemCount, viewportSize)
        }

        else -> {
            if (firstVisibleItemIndex == 0 && firstItemVisibleOffset == 0) VisibilityState.COMPLETELY_VISIBLE
            else if (visibleItems.none { it.index == 0 }) VisibilityState.NOT_VISIBLE
            else VisibilityState.PARTIALLY_VISIBLE
        }
    }

    return Pair(contentAboveState, contentBelowState)
}

private fun determineVisibilityState(
    visibleItems: List<LazyListItemInfo>,
    totalItemCount: Int,
    viewportSize: Int
): VisibilityState {
    val lastItem = visibleItems.lastOrNull()
    return if (lastItem != null && lastItem.index == totalItemCount - 1 && (lastItem.size + lastItem.offset) <= viewportSize) VisibilityState.COMPLETELY_VISIBLE
    else if (visibleItems.none { it.index == totalItemCount - 1 }) VisibilityState.NOT_VISIBLE
    else VisibilityState.PARTIALLY_VISIBLE
}

