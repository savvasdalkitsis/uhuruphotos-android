package com.savvasdalkitsis.uhuruphotos.foundation.activity.api

import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.scopes.ActivityRetainedScoped
import java.lang.ref.WeakReference
import javax.inject.Inject

@ActivityRetainedScoped
class CurrentActivityHolder @Inject constructor() {

    private var activity: WeakReference<FragmentActivity> = WeakReference(null)
    val currentActivity: FragmentActivity?
        get() = activity.get()

    fun onCreated(activity: FragmentActivity) {
        this.activity = WeakReference(activity)
    }

    fun onDestroy() {
        activity.clear()
    }
}