package com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashEffect
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashMutation
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.state.TrashState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Action

sealed class TrashAction : Action<TrashState, TrashEffect, TrashMutation, TrashActionsContext>