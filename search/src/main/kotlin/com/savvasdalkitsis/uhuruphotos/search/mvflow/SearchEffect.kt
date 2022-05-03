package com.savvasdalkitsis.uhuruphotos.search.mvflow

import androidx.compose.ui.geometry.Offset

sealed class SearchEffect {
    object HideKeyboard : SearchEffect()
    object ReloadApp : SearchEffect()
    object NavigateToEditServer : SearchEffect()
    object NavigateToSettings : SearchEffect()
    object ErrorSearching : SearchEffect()
    object NavigateToAllPeople : SearchEffect()
    object ErrorRefreshingPeople : SearchEffect()
    object NavigateToHeatMap : SearchEffect()

    data class NavigateToPerson(val personId: Int) : SearchEffect()
    data class OpenPhotoDetails(
        val id: String,
        val center: Offset,
        val scale: Float,
        val isVideo: Boolean,
    ) : SearchEffect()
}