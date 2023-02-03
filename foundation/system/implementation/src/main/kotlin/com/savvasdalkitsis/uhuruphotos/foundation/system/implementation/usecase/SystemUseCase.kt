package com.savvasdalkitsis.uhuruphotos.foundation.system.implementation.usecase

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.savvasdalkitsis.uhuruphotos.foundation.system.api.usecase.SystemUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class SystemUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) : SystemUseCase {

    override fun getAvailableSystemMemoryInMb(): Int =
        with(ActivityManager.MemoryInfo()) {
            (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(this)
            (availMem / (1024 * 1024)).toInt()
        }

    override fun getAvailableStorageInMb(): Int =
        with(StatFs(Environment.getExternalStorageDirectory().path)) {
            (blockSizeLong * blockCountLong / (1024 * 1024)).toInt()
        }
}