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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.objectweb.asm.Handle
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class LinkageCheckTest {

    @get:Rule
    val folder = TemporaryFolder()
    private val jars by lazy { TestJars(folder.root) }

    private val api by lazy {
        jars.jar(
            "group:api:2.0",
            type("api/Layout") { method("row", "(I)V") },
            type("api/Constants", interfaces = listOf("api/Defaults")),
            type("api/Defaults") {
                field("size", "I")
                method("defaulted", "()V")
            },
            type("api/Child", superName = "api/Layout", interfaces = listOf("api/Constants")),
            type("api/Orphan", superName = "gone/Base"),
        )
    }

    private fun findingsOf(vararg consumer: ByteArray): Set<Pair<Verdict, String>> =
        LinkageCheck(listOf(jars.platform(), api, jars.jar("group:consumer:1.0", *consumer)))
            .findings()
            .filter { it.library.coordinates == "group:consumer:1.0" }
            .mapTo(HashSet()) { it.verdict to it.reference.key }

    @Test
    fun `reports a call to an overload the resolved library no longer has`() {
        val consumer = type("consumer/Screen") {
            method("show", "()V") { call("api/Layout", "row", "(II)V") }
        }

        val findings = LinkageCheck(listOf(jars.platform(), api, jars.jar("group:consumer:1.0", consumer))).findings()

        val report = LinkageReport(findings, emptySet()).summary()
        assertTrue(report, "group:consumer:1.0: consumer.Screen uses missing method api.Layout.row(int, int)" in report)
        assertTrue(report, "absent from resolved group:api:2.0" in report)
        assertTrue(report, "allowlist key: group:consumer -> api/Layout.row(II)V" in report)
    }

    @Test
    fun `links members inherited from superclasses, interfaces, object and polymorphic handles`() {
        val consumer = type("consumer/Screen") {
            method("show", "()V") {
                call("api/Child", "row", "(I)V")
                call("api/Child", "defaulted", "()V")
                call("api/Defaults", "hashCode", "()I")
                call("[Lapi/Layout;", "clone", "()Ljava/lang/Object;")
                call("java/lang/invoke/MethodHandle", "invokeExact", "(I)V")
                visitFieldInsn(Opcodes.GETFIELD, "api/Child", "size", "I")
                visitTypeInsn(Opcodes.ANEWARRAY, "[I")
                visitLdcInsn("text")
                visitLdcInsn(Type.getMethodType("()V"))
            }
        }

        assertEquals(emptySet<Pair<Verdict, String>>(), findingsOf(consumer))
    }

    @Test
    fun `classifies every kind of missing reference`() {
        val start = Label()
        val end = Label()
        val consumer = type("consumer/Screen") {
            method("show", "()V") {
                visitTryCatchBlock(start, end, end, "api/MissingException")
                visitLabel(start)
                visitInsn(Opcodes.NOP)
                visitLabel(end)
                visitTypeInsn(Opcodes.NEW, "gone/Thing")
                visitMultiANewArrayInsn("[[Lapi/Missing;", 2)
                visitLdcInsn(Type.getObjectType("java/beans/Introspector"))
                visitLdcInsn(Handle(Opcodes.H_GETSTATIC, "api/Layout", "gone", "I", false))
                visitInvokeDynamicInsn(
                    "run",
                    "()Ljava/lang/Runnable;",
                    Handle(Opcodes.H_INVOKESTATIC, "api/Layout", "factory", "()V", false),
                    Handle(Opcodes.H_INVOKESTATIC, "api/Layout", "lambda", "()V", false),
                )
                call("android/os/Build", "newer", "()V")
                call("api/Orphan", "adopted", "()V")
            }
        }

        assertEquals(
            setOf(
                Verdict.MISSING_CLASS to "api/MissingException",
                Verdict.OPTIONAL_PACKAGE to "gone/Thing",
                Verdict.MISSING_CLASS to "api/Missing",
                Verdict.PLATFORM_GAP to "java/beans/Introspector",
                Verdict.MISSING_MEMBER to "api/Layout.goneI",
                Verdict.MISSING_MEMBER to "api/Layout.factory()V",
                Verdict.MISSING_MEMBER to "api/Layout.lambda()V",
                Verdict.PLATFORM_GAP to "android/os/Build.newer()V",
                Verdict.INCOMPLETE_HIERARCHY to "api/Orphan.adopted()V",
            ),
            findingsOf(consumer),
        )
    }

    @Test
    fun `merges duplicate classes shipped by two libraries`() {
        val shadow = jars.jar("group:shadow:1.0", type("api/Layout") { method("column", "()V") })
        val consumer = jars.jar(
            "group:consumer:1.0",
            type("consumer/Screen") {
                method("show", "()V") {
                    call("api/Layout", "row", "(I)V")
                    call("api/Layout", "column", "()V")
                }
            },
        )

        val findings = LinkageCheck(listOf(jars.platform(), api, shadow, consumer)).findings()

        assertEquals(emptyList<Finding>(), findings.filter { it.library == consumer })
    }
}
