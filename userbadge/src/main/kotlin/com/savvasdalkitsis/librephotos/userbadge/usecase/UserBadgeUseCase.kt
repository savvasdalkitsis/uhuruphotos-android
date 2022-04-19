package com.savvasdalkitsis.librephotos.userbadge.usecase

import androidx.work.WorkInfo
import androidx.work.WorkInfo.State.*
import com.savvasdalkitsis.librephotos.albums.worker.AlbumDownloadWorker
import com.savvasdalkitsis.librephotos.auth.usecase.ServerUseCase
import com.savvasdalkitsis.librephotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.librephotos.userbadge.view.state.SyncState.*
import com.savvasdalkitsis.librephotos.userbadge.view.state.UserInformationState
import com.savvasdalkitsis.librephotos.worker.usecase.WorkerStatusUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class UserBadgeUseCase @Inject constructor(
    private val userUseCase: com.savvasdalkitsis.librephotos.user.usecase.UserUseCase,
    private val workerStatusUseCase: WorkerStatusUseCase,
    private val photosUseCase: PhotosUseCase,
    private val serverUseCase: ServerUseCase,
) {

    fun getUserBadgeState(): Flow<UserInformationState> = combine(
        userUseCase.getUser(),
        workerStatusUseCase.monitorUniqueJobStatus(AlbumDownloadWorker.WORK_NAME),
        serverUseCase.observeServerUrl(),
    ) { user, status, serverUrl ->
        UserInformationState(
            avatarUrl = with(photosUseCase) { user.avatar?.toAbsoluteUrl() },
            syncState = when (status) {
                BLOCKED, CANCELLED, FAILED -> BAD
                ENQUEUED, SUCCEEDED -> GOOD
                RUNNING -> IN_PROGRESS
            },
            initials = user.firstName.initial() + user.lastName.initial(),
            userFullName = "${user.firstName} ${user.lastName}",
            serverUrl = serverUrl,
        )
    }

    private fun String?.initial() =
        orEmpty().firstOrNull()?.toString()?.uppercase() ?: ""
}

private operator fun WorkInfo.State.plus(other: WorkInfo.State): WorkInfo.State {
    if (this == RUNNING || other == RUNNING) return RUNNING
    if (this == FAILED || other == FAILED) return FAILED
    if (this == SUCCEEDED && other == SUCCEEDED) return SUCCEEDED
    return this
}
