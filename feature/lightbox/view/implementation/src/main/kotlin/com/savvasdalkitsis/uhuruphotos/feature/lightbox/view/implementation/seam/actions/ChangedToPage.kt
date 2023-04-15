package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions

import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffect
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ChangeCurrentIndex
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.SetOriginalFileIconState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.OriginalFileIconState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class ChangedToPage(val page: Int) : LightboxAction() {

    private var changePageJob: Job? = null

    context(LightboxActionsContext) override fun handle(
        state: LightboxState,
        effect: EffectHandler<LightboxEffect>
    ): Flow<LightboxMutation> = channelFlow {
        changePageJob?.cancel()
        changePageJob = null
        if (state.media.isNotEmpty()) {
            val page = page.coerceAtMost(state.media.size - 1)
            send(ChangeCurrentIndex(page))
            val photo = state.media[page]
            changePageJob = launch {
                merge<LightboxMutation>(
                    flow {
                        loadPhotoDetails(photo.id, photo.isVideo)
                    },
                    when (photo.id) {
                        is MediaId.Remote -> remoteMediaFileIconState(photo.id)
                        is MediaId.Local -> localMediaFileIconState(photo.id)
                        is MediaId.Group -> when (val id = photo.id.preferRemote) {
                            is MediaId.Remote -> remoteMediaFileIconState(id)
                            else -> localMediaFileIconState(id)
                        }
                    }
                ).collect { send(it) }
            }
        }
    }

    private fun localMediaFileIconState(mediaId: MediaId<*>) =
        flowOf(SetOriginalFileIconState(mediaId, OriginalFileIconState.HIDDEN))

    context(LightboxActionsContext)
    private fun remoteMediaFileIconState(mediaId: MediaId<*>): Flow<SetOriginalFileIconState> =
        mediaUseCase.observeOriginalFileDownloadStatus(mediaId)
            .map {
                when (it) {
                    WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING, WorkInfo.State.BLOCKED -> OriginalFileIconState.IN_PROGRESS
                    null, WorkInfo.State.SUCCEEDED -> OriginalFileIconState.IDLE
                    WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> OriginalFileIconState.ERROR
                }
            }
            .onStart { emit(OriginalFileIconState.IDLE) }
            .map { SetOriginalFileIconState(mediaId, it) }

}