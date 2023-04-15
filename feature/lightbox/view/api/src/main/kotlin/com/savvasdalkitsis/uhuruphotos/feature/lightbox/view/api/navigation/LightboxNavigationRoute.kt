package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute

data class LightboxNavigationRoute(
    val id: MediaId<*>,
    val isVideo: Boolean,
    val lightboxSequenceDataSource: LightboxSequenceDataSource = LightboxSequenceDataSource.Single,
    val showMediaSyncState: Boolean = false,
) : NavigationRoute