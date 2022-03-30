package com.savvasdalkitsis.librephotos.ui.view

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun LazyStaggeredGrid(
    columnCount: Int,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable LazyStaggeredGridScope.() -> Unit,
) {
    val states: Array<LazyListState> = (0 until columnCount)
        .map { rememberLazyListState() }
        .toTypedArray()
    val scope = rememberCoroutineScope()

    val scrollConnections: Array<NestedScrollConnection> = (0 until columnCount)
        .map { index ->
            remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                        val delta = available.y
                        scope.launch {
                            states.forEachIndexed { stateIndex, state ->
                                if (stateIndex != index) {
                                    state.scrollBy(-delta)
                                }
                            }
                        }
                        return Offset.Zero
                    }

                }
            }
        }
        .toTypedArray()

    val gridScope = LazyStaggeredGridScope(columnCount)
    content(gridScope)

    Row {
        for (index in 0 until columnCount) {
            LazyColumn(
                contentPadding = contentPadding,
                state = states[index],
                modifier = Modifier
                    .nestedScroll(scrollConnections[index])
                    .weight(1f)
            ) {
                for (item in gridScope.items[index]) {
                    item {
                        item()
                    }
                }
            }
        }
    }
}

class LazyStaggeredGridScope(
    private val columnCount: Int,
) {

    var currentIndex = 0
    val items: Array<MutableList<@Composable () -> Unit>> = Array(columnCount) { mutableListOf() }

    fun item(content: @Composable () -> Unit) {
        items[currentIndex % columnCount] += content
        currentIndex += 1
    }
}