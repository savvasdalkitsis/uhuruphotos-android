package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.maplibre.ui

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable

enum class Markers(
    val id: String,
    @DrawableRes
    val drawable: Int,
    @ColorInt
    val tint: Int,
) {
    Pin("pin", drawable.ic_pin, Color.RED)
}