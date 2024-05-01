package com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.domain.implementation.service.UserAlbumsService
import de.jensklingenberg.ktorfit.Ktorfit

object UserAlbumsModule {
    
            fun userAlbumsService(ktorfit: Ktorfit): UserAlbumsService = ktorfit.create()
}