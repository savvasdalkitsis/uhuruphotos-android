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

import java.io.File

data class Library(
    val coordinates: String,
    val file: File,
) {
    val isPlatform: Boolean get() = coordinates == PLATFORM
    val module: String get() = coordinates.substringBeforeLast(':')

    companion object {
        const val PLATFORM = "platform"

        fun parse(line: String): Library {
            val (coordinates, path) = line.split('\t', limit = 2)
            return Library(coordinates, File(path))
        }

        fun readAll(file: File): List<Library> =
            file.readLines().filter { it.isNotBlank() }.map(::parse)
    }
}
