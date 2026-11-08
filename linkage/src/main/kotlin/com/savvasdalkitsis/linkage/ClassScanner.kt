/*
Copyright 2026 Savvas Dalkitsis

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
package com.savvasdalkitsis.linkage

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class ClassScanner(private val library: Library) : ClassVisitor(Opcodes.ASM9) {

    val references = mutableSetOf<Reference>()
    private var name = ""
    private var superName: String? = null
    private var interfaces = emptyList<String>()
    private val methods = mutableSetOf<String>()
    private val fields = mutableSetOf<String>()

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?,
    ) {
        this.name = name
        this.superName = superName
        this.interfaces = interfaces.orEmpty().toList()
        (listOfNotNull(superName) + this.interfaces).mapNotNullTo(references, Reference::ofClass)
    }

    override fun visitField(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        value: Any?,
    ): FieldVisitor? {
        fields += "$name:$descriptor"
        return null
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?,
    ): MethodVisitor {
        methods += "$name$descriptor"
        return CodeScanner(references)
    }

    fun model() = ClassModel(name, superName, interfaces, methods, fields, library)

    companion object {
        fun read(bytes: ByteArray, library: Library, withCode: Boolean): ClassScanner {
            val scanner = ClassScanner(library)
            val code = if (withCode) 0 else ClassReader.SKIP_CODE
            ClassReader(bytes).accept(scanner, code or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
            return scanner
        }
    }
}
