package com.savvasdalkitsis.librephotos.search.mvflow

sealed class SearchEffect {
    object HideKeyboard : SearchEffect()
    object FocusSearchBar : SearchEffect()
    object ReloadApp : SearchEffect()
}