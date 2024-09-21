package com.savvasdalkitsis.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Ignore
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

    @Test
    @Ignore("This is violated a lot so will be used to slowly fix the issues")
    fun `UseCases should not use database layer classes directly`() {
        Konsist.scopeFromProject()
            .assertArchitecture {
                val useCases = Layer("UseCases", "..usecase..")
                val database = Layer("Database", "com.savvasdalkitsis.uhuruphotos.feature.db..")
                useCases.doesNotDependOn(database)
            }
    }
}