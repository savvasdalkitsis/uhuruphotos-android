package com.savvasdalkitsis.uhuruphotos.settings.viewmodel

import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsEffect.ShowMessage
import com.savvasdalkitsis.uhuruphotos.toaster.Toaster
import com.savvasdalkitsis.uhuruphotos.viewmodel.EffectHandler
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
