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
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.holder.CurrentActivityHolder
import dagger.hilt.android.scopes.ActivityRetainedScoped
import se.ansman.dagger.auto.AutoBind
import java.lang.ref.WeakReference
import javax.inject.Inject

@AutoBind
@ActivityRetainedScoped
class CurrentActivityHolder @Inject constructor() : CurrentActivityHolder {

    private var activity: WeakReference<FragmentActivity> = WeakReference(null)

    override val currentActivity: FragmentActivity?
        get() = activity.get()

    override fun onCreated(activity: FragmentActivity) {
        this.activity = WeakReference(activity)
    }

    override fun onDestroy() {
        activity.clear()
    }
}