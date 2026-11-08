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
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

class TestJars(private val folder: File) {

    fun jar(coordinates: String, vararg classes: ByteArray): Library {
        val file = File(folder, coordinates.replace(':', '_') + ".jar")
        JarOutputStream(file.outputStream()).use { out ->
            classes.forEach { bytes ->
                out.putNextEntry(JarEntry(ClassReader(bytes).className + ".class"))
                out.write(bytes)
                out.closeEntry()
            }
            out.putNextEntry(JarEntry("META-INF/versions/9/ignored/Gone.class"))
            out.write(classes.first())
            out.closeEntry()
        }
        return Library(coordinates, file)
    }

    fun platform(vararg classes: ByteArray) = jar(
        Library.PLATFORM,
        type("java/lang/Object", superName = null) { method("hashCode", "()I") },
        type("java/lang/invoke/MethodHandle") { method("invokeExact", "([Ljava/lang/Object;)Ljava/lang/Object;") },
        type("android/os/Build"),
        *classes,
    )
}

fun type(
    name: String,
    superName: String? = "java/lang/Object",
    interfaces: List<String> = emptyList(),
    members: ClassWriter.() -> Unit = {},
): ByteArray {
    val writer = ClassWriter(0)
    writer.visit(Opcodes.V21, Opcodes.ACC_PUBLIC, name, null, superName, interfaces.toTypedArray())
    writer.members()
    writer.visitEnd()
    return writer.toByteArray()
}

fun ClassWriter.method(name: String, descriptor: String, code: MethodVisitor.() -> Unit = {}) {
    visitMethod(Opcodes.ACC_PUBLIC, name, descriptor, null, null).apply {
        visitCode()
        code()
        visitInsn(Opcodes.RETURN)
        visitMaxs(0, 0)
        visitEnd()
    }
}

fun ClassWriter.field(name: String, descriptor: String) {
    visitField(Opcodes.ACC_PUBLIC, name, descriptor, null, null).visitEnd()
}

fun MethodVisitor.call(owner: String, name: String, descriptor: String) {
    visitMethodInsn(Opcodes.INVOKESTATIC, owner, name, descriptor, false)
}
