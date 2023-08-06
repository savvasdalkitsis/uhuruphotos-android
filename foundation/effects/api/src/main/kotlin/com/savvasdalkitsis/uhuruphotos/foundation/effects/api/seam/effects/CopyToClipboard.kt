package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

import android.content.ClipData
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

data class CopyToClipboard(val content: String) : CommonEffect() {
    context(CommonEffectsContext) override suspend fun handle() {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", content))
        toaster.show(string.copied_to_clipboard)
    }
}