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

import java.util.zip.ZipFile

object JarClasses {

    fun <T> read(library: Library, transform: (ByteArray) -> T): List<T> =
        ZipFile(library.file).use { zip ->
            zip.entries().asSequence()
                .filter { isLinkedClass(it.name) }
                .map { transform(zip.getInputStream(it).readBytes()) }
                .toList()
        }

    fun isLinkedClass(entry: String): Boolean =
        entry.endsWith(".class") && !entry.startsWith("META-INF/") && !entry.endsWith("module-info.class")
}
