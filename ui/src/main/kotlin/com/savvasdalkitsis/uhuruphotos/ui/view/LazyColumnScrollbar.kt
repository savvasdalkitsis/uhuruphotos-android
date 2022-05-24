/*
Copyright (c) 2021 nani

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.savvasdalkitsis.uhuruphotos.ui.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
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
import kotlinx.coroutines.launch
import kotlin.math.floor

/*
Copied from https://github.com/nanihadesuka/LazyColumnScrollbar

until https://github.com/nanihadesuka/LazyColumnScrollbar/pull/1 is merged
 */

/**
 * Scrollbar for LazyColumn
 *
 * @param rightSide true -> right,  false -> left
 * @param thickness Thickness of the scrollbar thumb
 * @param padding   Padding of the scrollbar
 * @param thumbMinHeight Thumb minimum height proportional to total scrollbar's height (eg: 0.1 -> 10% of total)
 */
@Composable
fun LazyColumnScrollbar(
	listState: LazyListState,
	rightSide: Boolean = true,
	thickness: Dp = 6.dp,
	padding: Dp = 8.dp,
	thumbMinHeight: Float = 0.1f,
	thumbColor: Color = Color(0xFF2A59B6),
	thumbSelectedColor: Color = Color(0xFF5281CA),
	thumbShape: Shape = CircleShape
)
{
	val coroutineScope = rememberCoroutineScope()

	var isSelected by remember { mutableStateOf(false) }

	var dragOffset by remember { mutableStateOf(0f) }

	fun LazyListItemInfo.fractionHiddenTop() = -offset.toFloat() / size.toFloat()
	fun LazyListItemInfo.fractionVisibleBottom(viewportEndOffset: Int) = (viewportEndOffset - offset).toFloat() / size.toFloat()

	fun normalizedThumbSize() = listState.layoutInfo.let {
		if (it.totalItemsCount == 0) return@let 0f
		val firstPartial = it.visibleItemsInfo.first().fractionHiddenTop()
		val lastPartial = 1f - it.visibleItemsInfo.last().fractionVisibleBottom(it.viewportEndOffset)
		val realVisibleSize = it.visibleItemsInfo.size.toFloat() - firstPartial - lastPartial
		realVisibleSize / it.totalItemsCount.toFloat()
	}.coerceAtLeast(thumbMinHeight)

	fun LazyListLayoutInfo.calcOffset(top:Float): Float {
		val bottom = visibleItemsInfo.last().run { index.toFloat() + fractionVisibleBottom(viewportEndOffset) } / totalItemsCount.toFloat()
		val offset = top * (1 - bottom) + bottom * bottom
		return offset * (1f - normalizedThumbSize())
	}

	fun normalizedOffsetPosition() = listState.layoutInfo.let {
		if (it.totalItemsCount == 0 || it.visibleItemsInfo.isEmpty())
			return@let 0f

		val top = it.visibleItemsInfo.first().run { index.toFloat() + fractionHiddenTop() } / it.totalItemsCount.toFloat()
		it.calcOffset(top)
	}



	fun setScrollOffset(newOffset: Float)
	{
		dragOffset = newOffset.coerceIn(0f, 1f)

		val exactIndex: Float = listState.layoutInfo.totalItemsCount.toFloat() * dragOffset
		val index: Int = floor(exactIndex).toInt()
		val remainder: Float = exactIndex - floor(exactIndex)

		coroutineScope.launch {
			listState.scrollToItem(index = index, scrollOffset = 0)
			val offset = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size?.let { it.toFloat() * remainder }?.toInt() ?: 0
			listState.animateScrollToItem(index = index, scrollOffset = offset)
		}
	}

	val isInAction = listState.isScrollInProgress || isSelected

	val alpha by animateFloatAsState(
		targetValue = if (isInAction) 1f else 0f,
		animationSpec = tween(durationMillis = if (isInAction) 75 else 500, delayMillis = if (isInAction) 0 else 500)
	)

	val displacement by animateFloatAsState(
		targetValue = if (isInAction) 0f else 14f,
		animationSpec = tween(durationMillis = if (isInAction) 75 else 500, delayMillis = if (isInAction) 0 else 500)
	)

	BoxWithConstraints(Modifier.fillMaxWidth()) {

		val dragState = rememberDraggableState { delta ->
			setScrollOffset(dragOffset + delta / constraints.maxHeight.toFloat()/(1f -normalizedThumbSize()))
		}

		BoxWithConstraints(
			Modifier
				.align(if (rightSide) Alignment.TopEnd else Alignment.TopStart)
				.alpha(alpha)
				.fillMaxHeight()
				.draggable(
					state = dragState,
					orientation = Orientation.Vertical,
					startDragImmediately = true,
					onDragStarted = { offset ->
						val newOffset = listState.layoutInfo.calcOffset(offset.y / constraints.maxHeight.toFloat())
						val currentOffset = normalizedOffsetPosition()

						if (currentOffset < newOffset && newOffset < currentOffset + normalizedThumbSize()) {
							dragOffset = currentOffset
						} else {
							setScrollOffset(newOffset)
						}

						isSelected = true
					},
					onDragStopped = {
						isSelected = false
					}
				)
				.absoluteOffset(x = if (rightSide) displacement.dp else -displacement.dp)
		) {

			Box(
				Modifier
					.align(Alignment.TopEnd)
					.graphicsLayer { translationY = constraints.maxHeight.toFloat() * normalizedOffsetPosition() }
					.padding(horizontal = padding)
					.width(thickness)
					.clip(thumbShape)
					.background(if (isSelected) thumbSelectedColor else thumbColor)
					.fillMaxHeight(normalizedThumbSize())
			)
		}
	}
}