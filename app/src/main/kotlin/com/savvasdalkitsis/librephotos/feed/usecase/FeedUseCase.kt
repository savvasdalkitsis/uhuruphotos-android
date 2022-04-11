package com.savvasdalkitsis.librephotos.feed.usecase

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.librephotos.feed.view.state.FeedDisplay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
class FeedUseCase @Inject constructor(
    preferences: FlowSharedPreferences,
) {
    private val preference = preferences.getEnum("feedDisplay", defaultValue = FeedDisplay.default)

    fun getFeedDisplay(): Flow<FeedDisplay> = preference.asFlow()

    suspend fun setFeedDisplay(feedDisplay: FeedDisplay) {
        preference.setAndCommit(feedDisplay)
    }
}