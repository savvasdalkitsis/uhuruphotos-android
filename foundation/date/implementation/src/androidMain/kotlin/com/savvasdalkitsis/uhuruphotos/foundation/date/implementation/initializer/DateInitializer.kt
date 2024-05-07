package com.savvasdalkitsis.uhuruphotos.foundation.date.implementation.initializer

import android.app.Application
import androidx.startup.AppInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCallbacks
import net.danlew.android.joda.JodaTimeInitializer

class DateInitializer : ApplicationCallbacks {

    override fun onAppCreated(app: Application) {
        AppInitializer.getInstance(app).initializeComponent(JodaTimeInitializer::class.java)
    }
}
