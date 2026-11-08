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

class LinkageReport(findings: List<Finding>, allowlist: Set<String>) {

    private val allowed = findings.filter { it.allowlistKey in allowlist }
    val breaks: List<Finding> = findings.filter { it.verdict.isBreak } - allowed.toSet()
    val unusedAllowlist: Set<String> = allowlist - allowed.mapTo(HashSet()) { it.allowlistKey }
    val noise: Map<Verdict, Int> = findings.filterNot { it.verdict.isBreak }.groupingBy { it.verdict }.eachCount()

    fun summary(): String = buildString {
        appendLine("${breaks.size} binary-incompatible references on the release runtime classpath:")
        breaks.sortedBy { it.describe() }.forEach {
            appendLine("  ${it.describe()}")
            appendLine("    allowlist key: ${it.allowlistKey}")
        }
        unusedAllowlist.forEach { appendLine("  allowlist entry no longer needed: $it") }
        appendLine("Filtered: allowlisted=${allowed.size} $noise")
    }
}
