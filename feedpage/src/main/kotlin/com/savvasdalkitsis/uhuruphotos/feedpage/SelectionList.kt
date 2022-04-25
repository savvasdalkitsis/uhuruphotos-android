package com.savvasdalkitsis.uhuruphotos.feedpage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class SelectionList @Inject constructor() {

    private var selectedIds = MutableStateFlow(emptySet<String>())

    val ids: Flow<Set<String>> = selectedIds

    suspend fun deselect(id: String) {
        selectedIds.emit(selectedIds.value - id)
    }

    suspend fun select(id: String) {
        selectedIds.emit(selectedIds.value + id)
    }

    suspend fun clear() {
        selectedIds.emit(emptySet())
    }
}