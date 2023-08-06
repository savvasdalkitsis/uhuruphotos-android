package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

import android.content.ClipboardManager
import android.content.Context
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.share.api.usecase.ShareUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.usecase.UiUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CommonEffectsContext @Inject constructor(
    internal val navigator: Navigator,
    internal val clipboardManager: ClipboardManager,
    internal val shareUseCase: ShareUseCase,
    internal val toaster: ToasterUseCase,
    internal val uiUseCase: UiUseCase,
    @ApplicationContext internal val context: Context,
)