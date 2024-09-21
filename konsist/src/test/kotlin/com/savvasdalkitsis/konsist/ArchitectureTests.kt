package com.savvasdalkitsis.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class ArchitectureTests {

    @Test
    fun `classes with 'UseCase' suffix should reside in 'usecsase' package`() {
        Konsist.scopeFromProject()
            .classesAndInterfaces()
            .withNameEndingWith("UseCase")
            .assertTrue { it.resideInPackage("..usecase..") }
    }

    @Test
    fun `classes with 'Repository' suffix should reside in 'repository' package`() {
        Konsist.scopeFromProject()
            .classesAndInterfaces()
            .withNameEndingWith("Repository")
            .assertTrue { it.resideInPackage("..repository..") }
    }
}