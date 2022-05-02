package com.savvasdalkitsis.uhuruphotos.feedpage.usecase

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplays
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FeedPageUseCase @Inject constructor(
    preferences: FlowSharedPreferences,
) {
    private val preference = preferences.getEnum("feedDisplay", defaultValue = FeedDisplays.default)

    fun getFeedDisplay(): Flow<FeedDisplays> = preference.asFlow()

    suspend fun setFeedDisplay(feedDisplay: FeedDisplays) {
        preference.setAndCommit(feedDisplay)
    }
}