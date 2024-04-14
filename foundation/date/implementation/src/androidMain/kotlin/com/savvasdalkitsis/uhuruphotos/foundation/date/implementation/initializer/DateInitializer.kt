package com.savvasdalkitsis.uhuruphotos.foundation.date.implementation.initializer

import android.app.Application
import androidx.startup.AppInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import net.danlew.android.joda.JodaTimeInitializer
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
internal class DateInitializer @Inject constructor(
): ApplicationCreated {

    override fun onAppCreated(app: Application) {
        AppInitializer.getInstance(app).initializeComponent(JodaTimeInitializer::class.java)
    }
}
