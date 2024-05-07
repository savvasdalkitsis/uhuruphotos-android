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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.initializer

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaPhotoColumns
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaVideoColumns
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationWindowCallbacks

class LocalMediaInitializer(
    private val localMediaWorkScheduler: LocalMediaWorkScheduler,
    private val contentResolver: ContentResolver,
) : ApplicationWindowCallbacks {

    private val mediaObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
        }
    }

    override fun onApplicationWindowCreated(window: FragmentActivity) {
        localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
        registerObserver(LocalMediaPhotoColumns.collection)
        registerObserver(LocalMediaVideoColumns.collection)
    }

    override fun onApplicationWindowDestroyed(window: FragmentActivity) {
        contentResolver.unregisterContentObserver(mediaObserver)
    }

    private fun registerObserver(uri: Uri) = contentResolver.registerContentObserver(
        uri,
        true,
        mediaObserver
    )
}