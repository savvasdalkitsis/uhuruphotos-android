package com.savvasdalkitsis.uhuruphotos.api.gallery.page.album.auto.navigation

import androidx.navigation.NavBackStackEntry

object AutoAlbumNavigationTarget {
    const val registrationName = "autoAlbum/{albumId}"
    fun name(id: Int) = registrationName.replace("{albumId}", id.toString())
    val NavBackStackEntry.albumId : Int get() =
        arguments!!.getString("albumId")!!.toInt()
}