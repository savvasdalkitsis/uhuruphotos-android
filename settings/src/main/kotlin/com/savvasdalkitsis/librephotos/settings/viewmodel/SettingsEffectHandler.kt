package com.savvasdalkitsis.librephotos.settings.viewmodel

import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsEffect.NavigateBack
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsEffect.ShowMessage
import com.savvasdalkitsis.librephotos.toaster.Toaster
import com.savvasdalkitsis.librephotos.viewmodel.EffectHandler
import javax.inject.Inject

class SettingsEffectHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val toaster: Toaster,
) : EffectHandler<SettingsEffect> {

    override suspend fun invoke(
        effect: SettingsEffect,
    ) {
        when(effect) {
            NavigateBack -> controllersProvider.navController!!.popBackStack()
            is ShowMessage -> toaster.show(effect.message)
        }
    }

}
