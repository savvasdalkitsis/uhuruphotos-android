package com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes

import androidx.annotation.StringRes
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

enum class ThemeContrast(@StringRes val label: Int) {
    NORMAL(string.contrast_normal),
    MEDIUM(string.contrast_medium),
    HIGH(string.contrast_high);

    companion object {
        val default = NORMAL
    }
}