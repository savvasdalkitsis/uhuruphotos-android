package com.savvasdalkitsis.uhuruphotos.person.viewmodel

import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonEffect.OpenPhotoDetails
import com.savvasdalkitsis.uhuruphotos.photos.navigation.PhotoNavigationTarget
import com.savvasdalkitsis.uhuruphotos.viewmodel.EffectHandler
import javax.inject.Inject

class PersonEffectHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
) : EffectHandler<PersonEffect> {

    override suspend fun invoke(effect: PersonEffect) {
        when (effect) {
            NavigateBack -> controllersProvider.navController!!.popBackStack()
            is OpenPhotoDetails -> controllersProvider.navController!!.navigate(with(effect) {
                PhotoNavigationTarget.name(id, center, scale, video)
            })
        }
    }

}
