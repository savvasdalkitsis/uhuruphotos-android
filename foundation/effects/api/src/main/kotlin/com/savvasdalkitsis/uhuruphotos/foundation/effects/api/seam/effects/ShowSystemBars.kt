package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

data object ShowSystemBars : CommonEffect() {
    context(CommonEffectsContext) override suspend fun handle() {
        uiUseCase.setSystemBarsVisibility(true)
    }
}