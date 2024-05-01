package com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.service.SearchService
import de.jensklingenberg.ktorfit.Ktorfit

object SearchModule {
    
            fun searchService(ktorfit: Ktorfit): SearchService = ktorfit.create()
}