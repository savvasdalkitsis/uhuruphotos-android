package com.savvasdalkitsis.librephotos.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.savvasdalkitsis.librephotos.toaster.Toaster
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IntentLauncher @Inject constructor(
    @ApplicationContext private val context: Context,
    private val toaster: Toaster,
) {

    fun launch(intent: Intent) {
        try {
            context.startActivity(intent.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: ActivityNotFoundException) {
            toaster.show("Could not find an app to open")
        }
    }

}
