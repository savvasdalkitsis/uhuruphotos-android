package com.savvasdalkitsis.uhuruphotos.ui.view

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun StaggeredGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    columnCount: Int,
    syncScrolling: Boolean = true,
    content: @Composable StaggeredGridScope.() -> Unit,
) {
    val states: Array<ScrollState> = (0 until columnCount)
        .map { rememberScrollState() }
        .toTypedArray()
    val scope = rememberCoroutineScope { Dispatchers.Main.immediate }
    val scroll = rememberScrollableState { delta ->
        if (syncScrolling) {
            scope.launch { states.forEach { it.scrollBy(-delta) } }
        }
        when {
            delta > 0 && states.all {
                it.value == 0
            } -> 0f
            else -> delta
        }
    }
    val gridScope = StaggeredGridScope(columnCount)
    content(gridScope)

    Box(
        modifier = modifier
            .scrollable(
                scroll,
                Orientation.Vertical,
                flingBehavior = ScrollableDefaults.flingBehavior()
            )
    ) {
        Row {
            for (index in 0 until columnCount) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(states[index])
                ) {
                    Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
                    gridScope.items[index].forEach { it() }
                    Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding()))
                }
            }
        }
    }
}

class StaggeredGridScope(
    private val columnCount: Int,
) {

    private var currentIndex = 0
    val items: Array<MutableList<@Composable () -> Unit>> = Array(columnCount) { mutableListOf() }

    fun item(content: @Composable () -> Unit) {
        items[currentIndex % columnCount] += content
        currentIndex += 1
    }
}