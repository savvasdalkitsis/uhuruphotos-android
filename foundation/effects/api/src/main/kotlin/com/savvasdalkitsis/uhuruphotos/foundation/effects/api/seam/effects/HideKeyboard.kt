package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

data object HideKeyboard : CommonEffect() {
    context(CommonEffectsContext) override suspend fun handle() {
        uiUseCase.hideKeyboard()
    }
}