package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

data object Vibrate : CommonEffect() {
    context(CommonEffectsContext) override suspend fun handle() {
        uiUseCase.performLongPressHaptic()
    }
}