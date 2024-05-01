package com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.service.PersonService
import de.jensklingenberg.ktorfit.Ktorfit

object PersonModule {
    
            fun personService(ktorfit: Ktorfit): PersonService = ktorfit.create()
}