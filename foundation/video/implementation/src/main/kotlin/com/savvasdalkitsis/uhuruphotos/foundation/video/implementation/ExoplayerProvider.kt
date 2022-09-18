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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ExoplayerProvider
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ExoplayerType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExoplayerProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cacheDataSourceFactory: CacheDataSource.Factory,
) : ExoplayerProvider {

    override fun getExoplayer(type: ExoplayerType): ExoPlayer = when (type) {
        ExoplayerType.Remote -> newRemotePlayer()
        ExoplayerType.Local -> newLocalPlayer()
    }

    private fun newRemotePlayer() = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
        .build()

    private fun newLocalPlayer() = ExoPlayer.Builder(context).build()
}