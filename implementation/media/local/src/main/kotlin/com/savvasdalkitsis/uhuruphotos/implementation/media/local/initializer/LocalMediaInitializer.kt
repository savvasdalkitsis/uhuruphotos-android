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
package com.savvasdalkitsis.uhuruphotos.implementation.media.local.initializer

import android.app.Application
import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.api.media.local.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.implementation.media.local.service.model.LocalMediaPhotoColumns
import com.savvasdalkitsis.uhuruphotos.implementation.media.local.service.model.LocalMediaVideoColumns
import javax.inject.Inject

internal class LocalMediaInitializer @Inject constructor(
    private val localMediaWorkScheduler: LocalMediaWorkScheduler,
    private val contentResolver: ContentResolver,
) : ApplicationCreated {

    override fun onAppCreated(app: Application) {
        localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
        registerObserver(LocalMediaPhotoColumns.collection)
        registerObserver(LocalMediaVideoColumns.collection)
    }

    private fun registerObserver(uri: Uri) = contentResolver.registerContentObserver(
        uri,
        true,
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
            }
        }
    )
}