package com.savvasdalktsis.uhuruphotos.foundation.download.implementation.initializer

import android.app.Application
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalktsis.uhuruphotos.foundation.download.api.usecase.DownloadUseCase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(DelicateCoroutinesApi::class)
@Singleton
class DownloadInitializer @Inject constructor(
    private val downloadUseCase: DownloadUseCase,
) : ApplicationCreated {

    override fun onAppCreated(app: Application) {
        GlobalScope.launch(Dispatchers.Default) {
            downloadUseCase.observeDownloading().collectLatest {
                if (it.isNotEmpty()) {
                    while (isActive) {
                        downloadUseCase.clearFailuresAndStale()
                        delay(1000)
                    }
                }
            }
        }
    }
}