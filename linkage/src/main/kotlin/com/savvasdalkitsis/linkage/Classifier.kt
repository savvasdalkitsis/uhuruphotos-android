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

class Classifier(private val classpath: Classpath) {

    private val resolver = Resolver(classpath)

    fun classify(reference: Reference): Verdict = when (resolver.resolve(reference)) {
        Resolution.FOUND -> Verdict.LINKED
        Resolution.UNKNOWN -> Verdict.INCOMPLETE_HIERARCHY
        Resolution.MISSING -> missing(reference)
    }

    private fun missing(reference: Reference): Verdict = when {
        reference.kind != ReferenceKind.CLASS -> missingMember(reference.owner)
        classpath.isLibraryPackage(reference.owner) -> Verdict.MISSING_CLASS
        classpath.isPlatformPackage(reference.owner) -> Verdict.PLATFORM_GAP
        else -> Verdict.OPTIONAL_PACKAGE
    }

    private fun missingMember(owner: String): Verdict =
        if (classpath[owner]?.library?.isPlatform == true) Verdict.PLATFORM_GAP else Verdict.MISSING_MEMBER
}
