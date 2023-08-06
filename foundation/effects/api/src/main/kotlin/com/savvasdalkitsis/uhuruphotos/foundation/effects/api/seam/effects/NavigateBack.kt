package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

data object NavigateBack : CommonEffect() {
    context(CommonEffectsContext) override suspend fun handle() {
        navigator.navigateBack()
    }
}