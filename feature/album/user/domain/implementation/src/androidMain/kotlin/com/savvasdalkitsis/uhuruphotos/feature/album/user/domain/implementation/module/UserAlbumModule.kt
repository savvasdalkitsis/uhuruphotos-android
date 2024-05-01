package com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.service.UserAlbumService
import de.jensklingenberg.ktorfit.Ktorfit

object UserAlbumModule {
    
            fun userAlbumService(ktorfit: Ktorfit): UserAlbumService = ktorfit.create()
}