/*
Code provided by https://blog.stackademic.com/jetpack-compose-multiplatform-scrollbar-scrolling-7c231a002ee1
 */
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scrollbar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private const val oneHundredPercentDecimal = 1f

private const val isEmpty = 0

@Composable
fun LazyColumnScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    rightSide: Boolean = true,
    alwaysShowScrollBar: Boolean = false,
    thickness: Dp = 6.dp,
    padding: Dp = 8.dp,
    thumbMinHeight: Float = 0.1f,
    thumbColor: Color,
    thumbSelectedColor: Color,
    thumbShape: Shape = CircleShape,
    selectionMode: ScrollbarSelectionMode,
    selectionActionable: ScrollbarSelectionActionable = ScrollbarSelectionActionable.Always,
    hideDelay: Duration = 400.toDuration(DurationUnit.MILLISECONDS),
    indicatorContent: (@Composable (index: Int, isThumbSelected: Boolean) -> Unit)? = null,
) {
    val firstVisibleItemIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }

    val coroutineScope = rememberCoroutineScope()

    var isSelected by remember { mutableStateOf(false) }

    var dragOffset by remember { mutableStateOf(0f) }

    val reverseLayout by remember { derivedStateOf { listState.layoutInfo.reverseLayout } }

    val realFirstVisibleItem by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.firstOrNull {
                it.index == listState.firstVisibleItemIndex
            }
        }
    }

    val isStickyHeaderInAction by remember {
        derivedStateOf {
            val realIndex = realFirstVisibleItem?.index ?: return@derivedStateOf false
            val firstVisibleIndex = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index
                ?: return@derivedStateOf false
            realIndex != firstVisibleIndex
        }
    }

    fun LazyListItemInfo.fractionHiddenTop(firstItemOffset: Int) =
        if (size == isEmpty) 0f else firstItemOffset / size.toFloat()

    fun LazyListItemInfo.fractionVisibleBottom(viewportEndOffset: Int) =
        if (size == isEmpty) 0f else (viewportEndOffset - offset).toFloat() / size.toFloat()

    val normalizedThumbSizeReal by remember {
        derivedStateOf {
            listState.layoutInfo.let {
                // If there are no items, return 0
                if (it.totalItemsCount == isEmpty) {
                    return@let 0f
                }

                val firstItem = realFirstVisibleItem ?: return@let 0f
                val firstPartial =
                    firstItem.fractionHiddenTop(listState.firstVisibleItemScrollOffset)
                val lastPartial =
                    oneHundredPercentDecimal - it.visibleItemsInfo.last().fractionVisibleBottom(
                        it.viewportEndOffset - it.afterContentPadding
                    )

                val realSize = it.visibleItemsInfo.size - if (isStickyHeaderInAction) 1 else isEmpty
                val realVisibleSize = realSize.toFloat() - firstPartial - lastPartial
                realVisibleSize / it.totalItemsCount.toFloat()
            }
        }
    }

    val normalizedThumbSize by remember {
        derivedStateOf {
            normalizedThumbSizeReal.coerceAtLeast(thumbMinHeight)
        }
    }

    fun offsetCorrection(top: Float): Float {
        val topRealMax = (oneHundredPercentDecimal - normalizedThumbSizeReal).coerceIn(0f,
            oneHundredPercentDecimal
        )
        if (normalizedThumbSizeReal >= thumbMinHeight) {
            return when {
                reverseLayout -> topRealMax - top
                else -> top
            }
        }

        val topMax = oneHundredPercentDecimal - thumbMinHeight
        return when {
            reverseLayout -> (topRealMax - top) * topMax / topRealMax
            else -> top * topMax / topRealMax
        }
    }

    fun offsetCorrectionInverse(top: Float): Float {
        if (normalizedThumbSizeReal >= thumbMinHeight)
            return top
        val topRealMax = oneHundredPercentDecimal - normalizedThumbSizeReal
        val topMax = oneHundredPercentDecimal - thumbMinHeight
        return top * topRealMax / topMax
    }

    val normalizedOffsetPosition by remember {
        derivedStateOf {
            listState.layoutInfo.let {
                if (it.totalItemsCount == isEmpty || it.visibleItemsInfo.isEmpty())
                    return@let 0f

                val firstItem = realFirstVisibleItem ?: return@let 0f
                val top = firstItem
                    .run { index.toFloat() + fractionHiddenTop(listState.firstVisibleItemScrollOffset) } / it.totalItemsCount.toFloat()
                offsetCorrection(top)
            }
        }
    }

    fun setDragOffset(value: Float) {
        val maxValue = (oneHundredPercentDecimal - normalizedThumbSize).coerceAtLeast(0f)
        dragOffset = value.coerceIn(0f, maxValue)
    }

    fun setScrollOffset(newOffset: Float) {
        setDragOffset(newOffset)
        val totalItemsCount = listState.layoutInfo.totalItemsCount.toFloat()
        val exactIndex = offsetCorrectionInverse(totalItemsCount * dragOffset)
        val index: Int = floor(exactIndex).toInt()
        val remainder: Float = exactIndex - floor(exactIndex)

        coroutineScope.launch {
            listState.scrollToItem(index = index, scrollOffset = isEmpty)
            val offset = realFirstVisibleItem
                ?.size
                ?.let { it.toFloat() * remainder }
                ?: 0f
            listState.scrollBy(offset)
        }
    }

    val isInAction = listState.isScrollInProgress || isSelected || alwaysShowScrollBar

    val isInActionSelectable = remember { mutableStateOf(isInAction) }
    val durationAnimationMillis = 500
    LaunchedEffect(isInAction) {
        if (isInAction) {
            isInActionSelectable.value = true
        } else {
            delay(timeMillis = durationAnimationMillis.toLong() + hideDelay.toLong(DurationUnit.MILLISECONDS))
            isInActionSelectable.value = false
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (isInAction) oneHundredPercentDecimal else 0f,
        animationSpec = tween(
            durationMillis = if (isInAction) 75 else durationAnimationMillis,
            delayMillis = if (isInAction) isEmpty else hideDelay.toInt(DurationUnit.MILLISECONDS)
        ),
        label = "scrollbar alpha value"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val maxHeightFloat = constraints.maxHeight.toFloat()

        // Aligning items to the top and towards the start/end based on `rightSide`
        Box(
            modifier = Modifier
                .align(if (rightSide) Alignment.TopEnd else Alignment.TopStart)
                .graphicsLayer(
                    translationY = maxHeightFloat * normalizedOffsetPosition
                )
        ) {
            // Using Column for vertical arrangement or Row for horizontal,
            // depending on your layout needs.
            Column { // or Row, if you need horizontal arrangement
                // Thumb Box
                Box(
                    modifier = Modifier
                        .padding(
                            start = if (rightSide) isEmpty.dp else padding,
                            end = if (!rightSide) isEmpty.dp else padding,
                        )
                        .clip(thumbShape)
                        .width(thickness)
                        .fillMaxHeight(normalizedThumbSize)
                        .alpha(alpha)
                        .background(if (isSelected) thumbSelectedColor else thumbColor)
                )

                // Optional indicator content
                if (indicatorContent != null) {
                    Box(
                        modifier = Modifier
                            .alpha(alpha)
                        // Additional modifiers to position this Box relative to the thumb Box
                        // might be needed depending on your exact requirements.
                    ) {
                        indicatorContent(firstVisibleItemIndex.value, isSelected)
                    }
                }
            }
        }

        @Composable
        fun DraggableBar() = Box(
            modifier = Modifier
                .align(if (rightSide) Alignment.TopEnd else Alignment.TopStart)
                .width(padding * 2 + thickness)
                .fillMaxHeight()
                .draggable(
                    state = rememberDraggableState { delta ->
                        val displace = if (reverseLayout) -delta else delta // side effect ?
                        if (isSelected) {
                            setScrollOffset(dragOffset + displace / maxHeightFloat)
                        }
                    },
                    orientation = Orientation.Vertical,
                    enabled = selectionMode != ScrollbarSelectionMode.Disabled,
                    startDragImmediately = true,
                    onDragStarted = onDragStarted@{ offset ->
                        if (maxHeightFloat <= 0f) return@onDragStarted
                        val newOffset = when {
                            reverseLayout -> (maxHeightFloat - offset.y) / maxHeightFloat
                            else -> offset.y / maxHeightFloat
                        }
                        val currentOffset = when {
                            reverseLayout -> oneHundredPercentDecimal - normalizedOffsetPosition - normalizedThumbSize
                            else -> normalizedOffsetPosition
                        }

                        when (selectionMode) {
                            ScrollbarSelectionMode.Full -> {
                                if (newOffset in currentOffset..(currentOffset + normalizedThumbSize))
                                    setDragOffset(currentOffset)
                                else
                                    setScrollOffset(newOffset)
                                isSelected = true
                            }

                            ScrollbarSelectionMode.Thumb -> {
                                if (newOffset in currentOffset..(currentOffset + normalizedThumbSize)) {
                                    setDragOffset(currentOffset)
                                    isSelected = true
                                }
                            }

                            ScrollbarSelectionMode.Disabled -> Unit
                        }
                    },
                    onDragStopped = {
                        isSelected = false
                    }
                )
        )

        val show = when (selectionActionable) {
            ScrollbarSelectionActionable.Always -> true
            ScrollbarSelectionActionable.WhenVisible -> isInActionSelectable.value
        }
        if (show) {
            DraggableBar()
        }
    }
}