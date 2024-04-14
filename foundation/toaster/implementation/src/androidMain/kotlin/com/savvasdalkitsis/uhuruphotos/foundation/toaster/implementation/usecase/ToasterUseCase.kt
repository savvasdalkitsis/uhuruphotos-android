/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.toaster.implementation.usecase

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onMain
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class ToasterUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) : ToasterUseCase {

    override fun show(@StringRes message: Int) {
        onMain {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}