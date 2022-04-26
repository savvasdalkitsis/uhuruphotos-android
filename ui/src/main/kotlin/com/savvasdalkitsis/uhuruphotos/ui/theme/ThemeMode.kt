package com.savvasdalkitsis.uhuruphotos.ui.theme

import androidx.annotation.DrawableRes
import com.savvasdalkitsis.uhuruphotos.icons.R

enum class ThemeMode(
    val friendlyName: String,
    @DrawableRes val icon: Int,
) {

    FOLLOW_SYSTEM("Follow system", R.drawable.ic_brightness_auto),
    DARK_MODE("Dark mode", R.drawable.ic_dark_mode),
    LIGHT_MODE("Light mode", R.drawable.ic_light_mode);

    companion object {
        val default = FOLLOW_SYSTEM
    }
}