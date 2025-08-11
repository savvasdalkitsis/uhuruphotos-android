package com.savvasdalkitsis.uhuruphotos.feature.user.view.api

import androidx.compose.runtime.compositionLocalOf
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.model.User

val LocalUser = compositionLocalOf<User> {
    throw NotImplementedError("CompositionLocal User not present")
}
