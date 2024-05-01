package com.savvasdalkitsis.uhuruphotos.feature.trash.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.trash.domain.implementation.service.TrashService
import de.jensklingenberg.ktorfit.Ktorfit

object TrashModule {
    
            fun autoAlbumService(ktorfit: Ktorfit): TrashService = ktorfit.create()
}