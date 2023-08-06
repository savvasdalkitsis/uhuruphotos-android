package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

data class UsePhotoAs(val url: String) : CommonEffect() {
    context(CommonEffectsContext) override suspend fun handle() {
        shareUseCase.usePhotoAs(url)
    }
}