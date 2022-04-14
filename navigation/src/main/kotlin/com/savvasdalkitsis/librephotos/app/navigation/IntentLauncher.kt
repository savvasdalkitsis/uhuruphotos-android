package com.savvasdalkitsis.librephotos.app.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IntentLauncher @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun launch(intent: Intent) {
        try {
            context.startActivity(intent.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Could not find an app to open", Toast.LENGTH_LONG).show()
        }
    }

}
