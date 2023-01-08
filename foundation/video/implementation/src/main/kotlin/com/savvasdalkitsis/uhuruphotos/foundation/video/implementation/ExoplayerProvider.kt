/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.foundation.video.implementation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.cache
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.mutableStateMapOf
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ExoplayerProvider
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ExoplayerType
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ExoplayerType.Local
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ExoplayerType.Remote
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExoplayerProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cacheDataSourceFactory: CacheDataSource.Factory,
    private val settingsUseCase: SettingsUseCase,
) : ExoplayerProvider {

    private val animatedUrls = mutableStateMapOf<String?, ExoPlayer>()

    @Composable
    override fun maybeCreateExoplayer(url: String?): ExoPlayer? =
        rememberUnreleasedIfNotNull(url) {
            maybeNewPlayer(url)
        }?.disposable(url)

    private fun maybeNewPlayer(url: String?) =
        when {
            url == null || settingsUseCase.getMaxAnimatedVideoThumbnails() <= animatedUrls.size -> null
            else -> newPlayer(url)
        }

    @Composable
    override fun createExoplayer(url: String): ExoPlayer =
        rememberUnreleased(url) {
            newPlayer(url)
        }.disposable(url)

    @Composable
    private fun ExoPlayer.disposable(url: String?) = apply {
        DisposableEffect(url) {
            onDispose {
                animatedUrls.remove(url)
                release(this@disposable)
            }
        }
    }

    private fun newPlayer(url: String): ExoPlayer {
        val type = ExoplayerType.fromUrl(url)
        return when (type) {
            Remote -> newRemotePlayer()
            Local -> newLocalPlayer()
        }.apply {
            animatedUrls[url] = this
        }
    }

    private fun release(exoPlayer: ExoPlayer) {
        exoPlayer.pause()
        exoPlayer.release()
    }

    private fun newRemotePlayer() = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
        .build()

    private fun newLocalPlayer() = ExoPlayer.Builder(context).build()


    @Composable
    private fun rememberUnreleasedIfNotNull(url: String?, block: () -> ExoPlayer?): ExoPlayer? =
        currentComposer.cache(url == null || currentComposer.changed(url) || !animatedUrls.containsKey(url), block)

    @Composable
    private fun rememberUnreleased(url: String, block: () -> ExoPlayer): ExoPlayer =
        currentComposer.cache(currentComposer.changed(url) || !animatedUrls.containsKey(url), block)

}