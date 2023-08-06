package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandlerWithContext
import javax.inject.Inject

class CommonEffectHandler @Inject constructor(
    effectsContext: CommonEffectsContext,
) : EffectHandler<CommonEffect> by EffectHandlerWithContext(effectsContext)