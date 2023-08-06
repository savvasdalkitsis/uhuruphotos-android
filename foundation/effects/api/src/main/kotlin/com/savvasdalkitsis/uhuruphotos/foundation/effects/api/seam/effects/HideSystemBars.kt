package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

data object HideSystemBars : CommonEffect() {
    context(CommonEffectsContext) override suspend fun handle() {
        uiUseCase.setSystemBarsVisibility(false)
    }
}