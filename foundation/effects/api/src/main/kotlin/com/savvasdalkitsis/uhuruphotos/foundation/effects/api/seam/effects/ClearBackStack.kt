package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

data object ClearBackStack : CommonEffect() {
    context(CommonEffectsContext) override suspend fun handle() {
        navigator.clearBackStack()
    }
}