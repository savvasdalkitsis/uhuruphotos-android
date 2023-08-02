/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.service

import android.content.Context
import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.work.UploadCompletionWorker
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleUseCase
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import javax.inject.Inject

class CompleteUploadsObserver @Inject constructor(
    private val workScheduleUseCase: WorkScheduleUseCase,
) : RequestObserverDelegate {

    override fun onSuccess(
        context: Context,
        uploadInfo: UploadInfo,
        serverResponse: ServerResponse
    ) {
        val uploadId = uploadInfo.uploadId
        workScheduleUseCase.scheduleNow(
            workName = UploadCompletionWorker.workName(uploadId),
            klass = UploadCompletionWorker::class,
        ) {
            putString(UploadCompletionWorker.KEY_UPLOAD_ID, uploadId)
        }
    }

    override fun onError(context: Context, uploadInfo: UploadInfo, exception: Throwable) {
    }

    override fun onCompleted(context: Context, uploadInfo: UploadInfo) {}

    override fun onCompletedWhileNotObserving() {}

    override fun onProgress(context: Context, uploadInfo: UploadInfo) {}

}
