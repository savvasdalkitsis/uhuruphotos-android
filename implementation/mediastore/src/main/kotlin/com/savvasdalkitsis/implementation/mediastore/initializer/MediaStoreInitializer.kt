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
package com.savvasdalkitsis.implementation.mediastore.initializer

import android.app.Application
import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.savvasdalkitsis.implementation.mediastore.service.model.MediaPhotoColumns
import com.savvasdalkitsis.implementation.mediastore.service.model.MediaVideoColumns
import com.savvasdalkitsis.uhuruphotos.api.initializer.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.api.mediastore.worker.MediaStoreWorkScheduler
import javax.inject.Inject

internal class MediaStoreInitializer @Inject constructor(
    private val mediaStoreWorkScheduler: MediaStoreWorkScheduler,
    private val contentResolver: ContentResolver,
) : ApplicationCreated {

    override fun onAppCreated(app: Application) {
        mediaStoreWorkScheduler.scheduleMediaStoreSyncNowIfNotRunning()
        registerObserver(MediaPhotoColumns.collection)
        registerObserver(MediaVideoColumns.collection)
    }

    private fun registerObserver(uri: Uri) = contentResolver.registerContentObserver(
        uri,
        true,
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                mediaStoreWorkScheduler.scheduleMediaStoreSyncNowIfNotRunning()
            }
        }
    )
}