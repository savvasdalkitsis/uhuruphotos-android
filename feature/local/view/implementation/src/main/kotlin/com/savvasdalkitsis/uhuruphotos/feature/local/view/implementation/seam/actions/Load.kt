package com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumEffect
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumMutation
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.ui.state.LocalAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

data class Load(val albumId: Int) : LocalAlbumAction() {
    context(LocalAlbumActionsContext) override fun handle(
        state: LocalAlbumState,
        effect: EffectHandler<LocalAlbumEffect>
    ) = localMediaUseCase.observePermissionsState()
        .flatMapLatest { when (it) {
            is LocalPermissions.RequiresPermissions -> flowOf(LocalAlbumMutation.AskForPermissions(it.deniedPermissions))
            else -> flow {
                LocalAlbumMutation.PermissionsGranted
                localMediaUseCase.refreshLocalMediaFolder(albumId)
            }
        } }
}