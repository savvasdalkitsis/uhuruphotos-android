package com.savvasdalkitsis.librephotos.feed.viewmodel

import com.savvasdalkitsis.librephotos.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.librephotos.albums.worker.AlbumDownloadWorker
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedAction
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedAction.LoadFeed
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedEffect
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedMutation
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedMutation.*
import com.savvasdalkitsis.librephotos.feed.view.state.FeedState
import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation
import com.savvasdalkitsis.librephotos.worker.usecase.WorkerStatusUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import net.pedroloureiro.mvflow.EffectSender
import net.pedroloureiro.mvflow.HandlerWithEffects
import javax.inject.Inject

class FeedHandler @Inject constructor(
    private val albumsUseCase: AlbumsUseCase,
    private val workerStatusUseCase: WorkerStatusUseCase,
) : HandlerWithEffects<FeedState, FeedAction, FeedMutation, FeedEffect> {

    override fun invoke(
        state: FeedState,
        action: FeedAction,
        effect: EffectSender<FeedEffect>
    ): Flow<FeedMutation> = when (action) {
        is LoadFeed -> channelFlow {
            send(Loading)
            albumsUseCase.getAlbums(refresh = false)
                .debounce(200)
                .map(::ShowAlbums)
                .onEach(this::send)
                .launchIn(CoroutineScope(Dispatchers.Default))

            workerStatusUseCase.monitorUniqueJobStatus(AlbumDownloadWorker.WORK_NAME)
                .map(::SyncJobUpdate)
                .onEach(this::send)
                .launchIn(CoroutineScope(Dispatchers.Default))

            awaitClose()
        }
    }
}
