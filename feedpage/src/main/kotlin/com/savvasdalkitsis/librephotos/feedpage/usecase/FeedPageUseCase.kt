package com.savvasdalkitsis.librephotos.feedpage.usecase

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.librephotos.feed.view.state.FeedDisplay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FeedPageUseCase @Inject constructor(
    preferences: FlowSharedPreferences,
) {
    private val preference = preferences.getEnum("feedDisplay", defaultValue = FeedDisplay.default)

    fun getFeedDisplay(): Flow<FeedDisplay> = preference.asFlow()

    suspend fun setFeedDisplay(feedDisplay: FeedDisplay) {
        preference.setAndCommit(feedDisplay)
    }
}