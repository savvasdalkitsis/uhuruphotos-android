package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

import android.content.Intent

data class NavigateToIntent(val intent: Intent) : CommonEffect() {
    context(CommonEffectsContext) override suspend fun handle() {
        navigator.navigateTo(intent)
    }
}