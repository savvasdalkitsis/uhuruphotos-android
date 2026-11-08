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

enum class Resolution { FOUND, MISSING, UNKNOWN }

class Resolver(private val classpath: Classpath) {

    private val hierarchies = HashMap<String, Hierarchy>()

    fun resolve(reference: Reference): Resolution = when (reference.kind) {
        ReferenceKind.CLASS -> if (classpath[reference.owner] != null) Resolution.FOUND else Resolution.MISSING
        ReferenceKind.FIELD -> member(reference.owner) { it.hasField(reference.name, reference.descriptor) }
        ReferenceKind.METHOD -> method(reference)
    }

    private fun method(reference: Reference): Resolution = when {
        reference.owner.startsWith("[") -> Resolution.FOUND
        reference.owner in SIGNATURE_POLYMORPHIC -> member(reference.owner) { it.hasMethodNamed(reference.name) }
        else -> member(reference.owner) { it.hasMethod(reference.name, reference.descriptor) }
    }

    private fun member(owner: String, matches: (ClassModel) -> Boolean): Resolution {
        val hierarchy = hierarchies.getOrPut(owner) { hierarchyOf(owner) }
        return when {
            hierarchy.models.any(matches) -> Resolution.FOUND
            hierarchy.complete -> Resolution.MISSING
            else -> Resolution.UNKNOWN
        }
    }

    private fun hierarchyOf(owner: String): Hierarchy {
        val pending = ArrayDeque(listOf(owner, OBJECT))
        val visited = LinkedHashSet<String>()
        while (pending.isNotEmpty()) {
            val name = pending.removeFirst()
            if (visited.add(name)) pending += classpath[name]?.ancestors.orEmpty()
        }
        val models = visited.mapNotNull { classpath[it] }
        return Hierarchy(models, complete = models.size == visited.size)
    }

    private class Hierarchy(val models: List<ClassModel>, val complete: Boolean)

    private companion object {
        const val OBJECT = "java/lang/Object"
        val SIGNATURE_POLYMORPHIC = setOf("java/lang/invoke/MethodHandle", "java/lang/invoke/VarHandle")
    }
}
