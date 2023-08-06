package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

import androidx.annotation.StringRes

data class ShowToast(@StringRes val message: Int) : CommonEffect() {
    context(CommonEffectsContext) override suspend fun handle() {
        toaster.show(message)
    }
}