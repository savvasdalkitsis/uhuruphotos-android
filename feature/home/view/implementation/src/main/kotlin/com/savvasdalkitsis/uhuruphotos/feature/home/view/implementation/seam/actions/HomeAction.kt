package com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeEffect
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeMutation
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.ui.state.HomeState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Action

sealed class HomeAction : Action<HomeState, HomeEffect, HomeMutation, HomeActionsContext>