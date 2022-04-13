package com.savvasdalkitsis.librephotos.navigation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IntentLauncher @Inject constructor(
    private val packageManager: PackageManager,
    @ApplicationContext private val context: Context
) {

    fun launch(intent: Intent) {
        intent.resolveActivity(packageManager)?.let {
            context.startActivity(intent.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

}
