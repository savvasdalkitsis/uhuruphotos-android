package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

data class ShareMultiple(val urls: List<String>) : CommonEffect() {
    context(CommonEffectsContext) override suspend fun handle() {
        shareUseCase.shareMultiple(urls)
    }
}