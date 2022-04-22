package com.savvasdalkitsis.librephotos.settings.viewmodel

import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsEffect.NavigateBack
import com.savvasdalkitsis.librephotos.viewmodel.EffectHandler
import javax.inject.Inject

class SettingsEffectHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
) : EffectHandler<SettingsEffect> {

    override suspend fun invoke(
        effect: SettingsEffect,
    ) {
        when(effect) {
            NavigateBack -> controllersProvider.navController!!.popBackStack()
        }
    }

}
