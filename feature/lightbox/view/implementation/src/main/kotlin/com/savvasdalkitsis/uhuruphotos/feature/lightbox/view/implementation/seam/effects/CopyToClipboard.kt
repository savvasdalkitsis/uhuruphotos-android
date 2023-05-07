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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.effects

import android.content.ClipData
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffectsContext
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R

data class CopyToClipboard(val content: String) : LightboxEffect() {
    context(LightboxEffectsContext) override suspend fun handle() {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", content))
        toasterUseCase.show(R.string.copied_to_clipboard)
    }
}