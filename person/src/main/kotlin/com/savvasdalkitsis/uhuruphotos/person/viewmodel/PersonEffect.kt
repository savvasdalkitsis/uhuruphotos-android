package com.savvasdalkitsis.uhuruphotos.person.viewmodel

import androidx.compose.ui.geometry.Offset

sealed class PersonEffect {
    data class OpenPhotoDetails(
        val id: String,
        val center: Offset,
        val scale: Float,
        val video: Boolean
    ) : PersonEffect()

    object NavigateBack : PersonEffect()
}
