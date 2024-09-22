package com.savvasdalkitsis.konsist

import androidx.lifecycle.ViewModel
import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.properties
import com.lemonappdev.konsist.api.ext.list.withAnnotationNamed
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.ext.list.withParentClassOf
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Ignore
import org.junit.Test

class ArchitectureTests {

    @Test
    fun `classes with 'UseCase' suffix should reside in 'usecsase' package`() {
        Konsist.scopeFromProject()
            .classesAndInterfacesAndObjects()
            .withNameEndingWith("UseCase")
            .assertTrue { it.resideInPackage("..usecase..") }
    }

    @Test
    fun `classes with 'Repository' suffix should reside in 'repository' package`() {
        Konsist.scopeFromProject()
            .classesAndInterfacesAndObjects()
            .withNameEndingWith("Repository")
            .assertTrue { it.resideInPackage("..repository..") }
    }

    @Test
    fun `ui state classes have a State suffix`() {
        Konsist.scopeFromProject()
            .classesAndInterfacesAndObjects()
            .filter { !it.hasModifier(KoModifier.COMPANION) }
            .withPackage("..ui.state..")
            .assertTrue { it.hasNameEndingWith("State") }
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

    @Test
    fun `package name must match file path`() {
        Konsist
            .scopeFromProject()
            .packages
            .filter { it.hasNameContaining("{{") } // exclude cookiecutter templates
            .assertTrue { it.hasMatchingPath }
    }

    @Test
    fun `no wildcard imports allowed`() {
        Konsist
            .scopeFromProject()
            .imports
            .assertFalse { it.isWildcard }
    }

    @Test
    fun `classes extending 'ViewModel' should have 'ViewModel' suffix`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withParentClassOf(ViewModel::class)
            .assertTrue { it.name.endsWith("ViewModel") }
    }

    @Test
    fun `compose state classes are marked as immutable`() {
        Konsist
            .scopeFromProject()
            .classes()
            .filter { !it.hasModifier(KoModifier.ENUM) }
            .withPackage("..ui.state..")
            .assertTrue { it.hasAnnotationWithName("androidx.compose.runtime.Immutable") }
    }

    @Test
    fun `compose state classes do not use standard kotlin collections`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withPackage("..ui.state..")
            .assertTrue {
                it.properties().none { prop ->
                    val name = prop.type?.name.orEmpty()
                    name.startsWith("List<") || name.startsWith("Map<") || name.startsWith("Set<")
                }
            }
    }

    @Test
    fun `classes annotated with 'JsonClass' have all properties annotated with 'Json'`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withAnnotationNamed("com.squareup.moshi.JsonClass")
            .properties()
            .assertTrue {
                it.hasAnnotationWithName("com.squareup.moshi.Json")
            }
    }
}