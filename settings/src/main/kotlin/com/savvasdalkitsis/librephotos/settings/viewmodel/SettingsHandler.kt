package com.savvasdalkitsis.librephotos.settings.viewmodel

import com.savvasdalkitsis.librephotos.settings.usecase.CacheUseCase
import com.savvasdalkitsis.librephotos.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.librephotos.settings.view.state.SettingsState
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction.*
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsMutation.*
import com.savvasdalkitsis.librephotos.viewmodel.Handler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

internal class SettingsHandler @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val cacheUseCase: CacheUseCase,
) : Handler<SettingsState, SettingsEffect, SettingsAction, SettingsMutation> {

    override fun invoke(
        state: SettingsState,
        action: SettingsAction,
        effect: suspend (SettingsEffect) -> Unit
    ): Flow<SettingsMutation> = when (action) {
        LoadSettings -> merge(
            settingsUseCase.observeDiskCacheMaxLimit()
                .map(::DisplayDiskCacheMaxLimit),
            settingsUseCase.observeMemCacheMaxLimit()
                .map(::DisplayMemCacheMaxLimit),
            cacheUseCase.observeDiskCacheCurrentUse()
                .map(::DisplayDiskCacheCurrentUse),
            cacheUseCase.observeMemCacheCurrentUse()
                .map(::DisplayMemCacheCurrentUse)
        )
        NavigateBack -> flow {
            effect(SettingsEffect.NavigateBack)
        }
        is ChangeDiskCache -> flow {
            settingsUseCase.setDiskCacheMaxLimit(action.sizeInMb.toInt())
        }
        ClearDiskCache -> flow {
            cacheUseCase.clearDiskCache()
        }
        is ChangeMemCache -> flow {
            settingsUseCase.setMemCacheMaxLimit(action.sizeInMb.toInt())
        }
        ClearMemCache -> flow {
            cacheUseCase.clearMemCache()
        }
    }

}
