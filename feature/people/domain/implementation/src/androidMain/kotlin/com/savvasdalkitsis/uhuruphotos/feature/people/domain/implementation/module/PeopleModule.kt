package com.savvasdalkitsis.uhuruphotos.feature.people.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.people.domain.implementation.service.PeopleService
import de.jensklingenberg.ktorfit.Ktorfit

object PeopleModule {
    
            fun peopleService(ktorfit: Ktorfit): PeopleService = ktorfit.create()
}