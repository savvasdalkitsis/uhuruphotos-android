package com.savvasdalkitsis.librephotos.initializer

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationInitializer @Inject constructor(
    private val listeners: Set<@JvmSuppressWildcards ApplicationCreated>,
) {

    fun onCreated() {
        listeners.forEach(ApplicationCreated::onAppCreated)
    }
}