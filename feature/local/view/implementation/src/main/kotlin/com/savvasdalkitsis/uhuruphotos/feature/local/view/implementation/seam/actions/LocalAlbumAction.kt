package com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumEffect
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumMutation
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.ui.state.LocalAlbumState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Action

sealed class LocalAlbumAction :
    Action<LocalAlbumState, LocalAlbumEffect, LocalAlbumMutation, LocalAlbumActionsContext>