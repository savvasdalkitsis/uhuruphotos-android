package com.savvasdalkitsis.uhuruphotos.search.mvflow

sealed class SearchEffect {
    object HideKeyboard : SearchEffect()
    object FocusSearchBar : SearchEffect()
    object ReloadApp : SearchEffect()
    object NavigateToEditServer : SearchEffect()
    object NavigateToSettings : SearchEffect()
}