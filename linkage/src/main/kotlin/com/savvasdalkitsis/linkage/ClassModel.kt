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

data class ClassModel(
    val name: String,
    val superName: String?,
    val interfaces: List<String>,
    val methods: Set<String>,
    val fields: Set<String>,
    val library: Library,
) {
    val ancestors: List<String> get() = listOfNotNull(superName) + interfaces

    fun hasMethod(name: String, descriptor: String): Boolean = "$name$descriptor" in methods

    fun hasMethodNamed(name: String): Boolean = methods.any { it.startsWith("$name(") }

    fun hasField(name: String, descriptor: String): Boolean = "$name:$descriptor" in fields

    fun mergedWith(other: ClassModel): ClassModel = copy(
        methods = methods + other.methods,
        fields = fields + other.fields,
    )
}
