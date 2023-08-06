package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

data class Share(val url: String) : CommonEffect() {
    context(CommonEffectsContext) override suspend fun handle() {
        shareUseCase.share(url)
    }
}