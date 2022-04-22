package com.savvasdalkitsis.librephotos.search.mvflow

sealed class SearchAction {

    object Initialise : SearchAction()
    object ClearSearch : SearchAction()
    object UserBadgePressed : SearchAction()
    object DismissAccountOverview : SearchAction()
    object LogOut : SearchAction()
    object EditServer : SearchAction()
    object SettingsClick : SearchAction()

    data class ChangeQuery(val query: String) : SearchAction()
    data class SearchFor(val query: String) : SearchAction()
    data class ChangeFocus(val focused: Boolean) : SearchAction()
}