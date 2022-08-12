package com.savvasdalkitsis.uhuruphotos.api.gallery.page.album.user.navigation

import androidx.navigation.NavBackStackEntry

object UserAlbumNavigationTarget {
    const val registrationName = "userAlbum/{albumId}"
    fun name(id: Int) = registrationName.replace("{albumId}", id.toString())
    val NavBackStackEntry.albumId : Int get() =
        arguments!!.getString("albumId")!!.toInt()
}