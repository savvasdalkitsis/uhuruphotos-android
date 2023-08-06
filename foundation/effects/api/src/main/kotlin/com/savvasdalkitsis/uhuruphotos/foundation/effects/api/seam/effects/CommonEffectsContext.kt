package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

import android.content.Context
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.share.api.usecase.ShareUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.usecase.UiUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CommonEffectsContext @Inject constructor(
    val navigator: Navigator,
    val toaster: ToasterUseCase,
    val uiUseCase: UiUseCase,
    @ApplicationContext internal val context: Context,
)