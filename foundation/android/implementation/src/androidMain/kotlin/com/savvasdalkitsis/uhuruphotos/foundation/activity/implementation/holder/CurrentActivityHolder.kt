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
package com.savvasdalkitsis.uhuruphotos.foundation.activity.implementation.holder

import androidx.fragment.app.FragmentActivity
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.holder.CurrentActivityHolder
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationWindowCallbacks
import java.lang.ref.WeakReference

class CurrentActivityHolder : CurrentActivityHolder, ApplicationWindowCallbacks {

    override fun priority(): Int = Int.MIN_VALUE

    private var _activity: WeakReference<FragmentActivity> = WeakReference(null)

    override val currentActivity: FragmentActivity?
        get() = _activity.get()

    override fun onApplicationWindowCreated(window: FragmentActivity) {
        _activity = WeakReference(window)
    }

    override fun onApplicationWindowDestroyed(window: FragmentActivity) {
        _activity.clear()
    }
}