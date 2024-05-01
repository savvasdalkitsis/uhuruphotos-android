package com.savvasdalkitsis.uhuruphotos.feature.user.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.user.domain.implementation.service.UserService
import de.jensklingenberg.ktorfit.Ktorfit

internal object UserModule {
    
            fun userService(ktorfit: Ktorfit): UserService = ktorfit.create()
}