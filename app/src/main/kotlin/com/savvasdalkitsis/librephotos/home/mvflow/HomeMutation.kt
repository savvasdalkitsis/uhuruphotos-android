package com.savvasdalkitsis.librephotos.home.mvflow

import com.savvasdalkitsis.librephotos.feed.view.FeedState

sealed class HomeMutation {
    object Loading : HomeMutation()
    object FinishedLoading : HomeMutation()
    data class ShowAlbums(val feedState: FeedState) : HomeMutation()
}
