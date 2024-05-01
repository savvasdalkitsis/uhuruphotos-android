package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.implementation.service.AutoAlbumsService
import de.jensklingenberg.ktorfit.Ktorfit

object AutoAlbumsModule {
    
            fun autoAlbumsService(ktorfit: Ktorfit): AutoAlbumsService = ktorfit.create()
}