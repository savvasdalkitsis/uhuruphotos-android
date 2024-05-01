/*
Copyright 2024 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
@file:OptIn(KspExperimental::class)

package com.savvasdalkitsis.uhuruphotos.foundation.initializer.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import java.io.FileWriter
import java.io.OutputStream
import kotlin.reflect.KClass

class InitializerProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
): SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver
            .getDeclarationsFromPackage("com.savvasdalkitsis.uhuruphotos")
            .filter { it.isAnnotationPresent(Class.forName("com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.AutoInitialize").kotlin as KClass<Annotation>) }
//            .getSymbolsWithAnnotation("com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.AutoInitialize")
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.OBJECT }

        if (!symbols.iterator().hasNext()) return emptyList()

        codeGenerator.createNewFile(
            dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
            packageName = "com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.auto",
            fileName = "AutoInitialized"
        ).bufferedWriter().use {
            with(it) {
                write("package com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.auto"); newLine()
                newLine()
                write("data object AutoInitialized {"); newLine()
                write("  val objects = listOf("); newLine();
                symbols.iterator().forEachRemaining { obj ->
                write("    ${obj.qualifiedName?.asString()},"); newLine() }
                write("  )"); newLine()
                newLine()
                write("  fun init() {}"); newLine()
                write("}")
            }
        }

        return emptyList()
    }
}