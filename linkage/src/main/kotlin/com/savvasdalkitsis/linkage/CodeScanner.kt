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

import org.objectweb.asm.Handle
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class CodeScanner(private val references: MutableSet<Reference>) : MethodVisitor(Opcodes.ASM9) {

    override fun visitTypeInsn(opcode: Int, type: String) {
        addClass(type)
    }

    override fun visitMultiANewArrayInsn(descriptor: String, numDimensions: Int) {
        addClass(descriptor)
    }

    override fun visitTryCatchBlock(start: Label?, end: Label?, handler: Label?, type: String?) {
        type?.let(::addClass)
    }

    override fun visitFieldInsn(opcode: Int, owner: String, name: String, descriptor: String) {
        addMember(Reference(ReferenceKind.FIELD, owner, name, descriptor))
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String,
        name: String,
        descriptor: String,
        isInterface: Boolean,
    ) {
        addMember(Reference(ReferenceKind.METHOD, owner, name, descriptor))
    }

    override fun visitInvokeDynamicInsn(
        name: String,
        descriptor: String,
        bootstrapMethodHandle: Handle,
        vararg bootstrapMethodArguments: Any,
    ) {
        addConstant(bootstrapMethodHandle)
        bootstrapMethodArguments.forEach(::addConstant)
    }

    override fun visitLdcInsn(value: Any) {
        addConstant(value)
    }

    private fun addConstant(value: Any) {
        when (value) {
            is Handle -> addMember(Reference.ofHandle(value))
            is Type -> value.takeIf { it.sort != Type.METHOD }?.let { addClass(it.internalName) }
        }
    }

    private fun addClass(internalName: String) {
        Reference.ofClass(internalName)?.let(references::add)
    }

    private fun addMember(reference: Reference) {
        addClass(reference.owner)
        references += reference
    }
}
