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

class Classpath(models: List<ClassModel>) {

    private val classes: Map<String, ClassModel> = models.groupBy { it.name }
        .mapValues { (_, copies) -> copies.reduce(ClassModel::mergedWith) }
    private val libraryPackages = packagesOf(models.filterNot { it.library.isPlatform })
    private val platformPackages = packagesOf(models.filter { it.library.isPlatform })

    operator fun get(name: String): ClassModel? = classes[name]

    fun isLibraryPackage(className: String) = packageOf(className) in libraryPackages

    fun isPlatformPackage(className: String) =
        packageOf(className) in platformPackages || JDK_PREFIXES.any(className::startsWith)

    companion object {
        private val JDK_PREFIXES = listOf("java/", "sun/", "jdk/")

        fun packageOf(className: String) = className.substringBeforeLast('/', "")

        fun load(libraries: List<Library>): Classpath = Classpath(
            libraries.flatMap { library ->
                JarClasses.read(library) { ClassScanner.read(it, library, withCode = false).model() }
            }
        )

        private fun packagesOf(models: List<ClassModel>) = models.mapTo(HashSet()) { packageOf(it.name) }
    }
}
