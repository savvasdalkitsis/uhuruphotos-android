package com.savvasdalkitsis.uhuruphotos.feature.site.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.site.domain.implementation.service.SiteService
import de.jensklingenberg.ktorfit.Ktorfit

object SiteModule {
    
            fun siteService(ktorfit: Ktorfit): SiteService = ktorfit.create()
}