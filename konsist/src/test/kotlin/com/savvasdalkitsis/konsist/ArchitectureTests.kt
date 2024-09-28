package com.savvasdalkitsis.konsist

import androidx.lifecycle.ViewModel
import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.container.KoScope
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

    private val useCaseLayer = Layer("UseCases", "..usecase..")
    private val databaseLayer = Layer("Database", "com.savvasdalkitsis.uhuruphotos.feature.db..")

    @Test
    fun `all files have license header`() {
        Konsist.scopeFromProject()
            .files
            .assertTrue { it.text.contains("Apache License, Version 2.0") }
    }

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
    fun `domain model classes have a Model suffix`() {
        Konsist.scopeFromProject()
            .classesAndInterfacesAndObjects()
            .filter { !it.hasModifier(KoModifier.COMPANION) }
            .withPackage("..model..")
            .assertTrue { it.hasNameEndingWith("Model") }
    }

    @Test
    @Ignore("This is violated a lot so will be used to slowly fix the issues")
    fun `UseCases should not use database layer classes directly`() {
        Konsist.scopeFromProject()
            .assertArchitecture {
                useCaseLayer.doesNotDependOn(databaseLayer)
            }
    }

    @Test
    @Ignore("This is violated a lot so will be used to slowly fix the issues")
    fun `UseCases do not use service http response models`() {
        Konsist.scopeFromProject()
            .assertArchitecture {
                val serviceHttpResponseModelLayer = Layer("HttpServiceResponseModelsLayer", "..http.response..")

                useCaseLayer.doesNotDependOn(serviceHttpResponseModelLayer)
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

    @Test
    fun `http ResponseData classes are in the correct package`() {
        Konsist.scopeFromProject()
            .httpResponseClasses()
            .assertTrue {
                it.resideInPackage("..service.http.response")
            }
    }

    @Test
    fun `http ResponseData classes are json classes`() {
        Konsist.scopeFromProject()
            .httpResponseClasses()
            .assertTrue {
                it.hasAnnotationWithName("com.squareup.moshi.JsonClass")
            }
    }

    @Test
    fun `http RequestData classes are in the correct package`() {
        Konsist.scopeFromProject()
            .httpRequestClasses()
            .assertTrue {
                it.resideInPackage("..service.http.request")
            }
    }

    @Test
    fun `http RequestData classes are json classes`() {
        Konsist.scopeFromProject()
            .httpRequestClasses()
            .assertTrue {
                it.hasAnnotationWithName("com.squareup.moshi.JsonClass")
            }
    }

    @Test
    fun `retrofit services are in the right package`() {
        Konsist.scopeFromProject()
            .retrofitServices()
            .assertTrue {
                it.resideInPackage("..service.http")
            }
    }

    @Test
    fun `retrofit services only return ResponseData types`() {
        Konsist.scopeFromProject()
            .retrofitServices()
            .assertTrue {
                it.hasAllFunctions { function ->
                    val name = function.returnType?.name.orEmpty()
                    name.endsWith("ResponseData") ||
                            (name.startsWith("Response<") && name.endsWith("ResponseData>")) ||
                            name.isEmpty()
                }
            }
    }

    @Test
    fun `retrofit services only have raw or RequestData types as parameters`() {
        val rawTypes = setOf("Int", "String", "Boolean", "Float", "Double", "Long", "MultipartBody.Part")
        Konsist.scopeFromProject()
            .retrofitServices()
            .assertTrue {
                it.hasAllFunctions { function ->
                    function.hasAllParameters { param ->
                        param.type.name in rawTypes || param.type.name.endsWith("RequestData")
                    }
                }
            }
    }

    private fun KoScope.httpResponseClasses() = classesAndInterfacesAndObjects()
        .filter { !it.hasModifier(KoModifier.COMPANION) }
        .withNameEndingWith("ResponseData")

    private fun KoScope.httpRequestClasses() = classesAndInterfacesAndObjects()
        .filter { !it.hasModifier(KoModifier.COMPANION) }
        .withNameEndingWith("RequestData")

    private fun KoScope.retrofitServices() = interfaces().filter {
        it.hasFunction { f ->
            f.annotations.any { annotation ->
                annotation.fullyQualifiedName.orEmpty().contains("retrofit2.http")
            }
        }
    }

}