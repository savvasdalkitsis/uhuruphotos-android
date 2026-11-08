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

class LinkageCheck(private val libraries: List<Library>) {

    fun findings(): List<Finding> {
        val classpath = Classpath.load(libraries)
        val classifier = Classifier(classpath)
        return libraries.filterNot { it.isPlatform }.flatMap { library ->
            JarClasses.read(library) { ClassScanner.read(it, library, withCode = true) }
                .flatMap { scanner -> scanner.findings(classifier, classpath) }
                .distinctBy { it.reference }
        }
    }

    private fun ClassScanner.findings(classifier: Classifier, classpath: Classpath): List<Finding> {
        val model = model()
        return references.mapNotNull { reference ->
            classifier.classify(reference).takeIf { it != Verdict.LINKED }?.let { verdict ->
                Finding(model.library, model.name, reference, verdict, classpath[reference.owner]?.library)
            }
        }
    }
}
