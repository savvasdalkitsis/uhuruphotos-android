/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.http.api

import org.apache.commons.validator.routines.RegexValidator
import org.apache.commons.validator.routines.UrlValidator
import org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS

private val httpValidator = UrlValidator(arrayOf("http"), RegexValidator(".*"), ALLOW_LOCAL_URLS)
private val httpsValidator = UrlValidator(arrayOf("https"), RegexValidator(".*"), ALLOW_LOCAL_URLS)

val String.isValidUrlOrDomain get() = isValidUrl || "https://$this".isValidUrl

val String.isHttpUrl get() = httpValidator.isValid(this.trim())

val String.prefixedWithHttpsIfNeeded get() = when {
    needsHttpsPrefix -> "https://$this"
    else -> this
}.trim()

private val String.needsHttpsPrefix get() =
    !isValidUrl && "https://$this".isValidUrl

private val String.isValidUrl get() = isHttpUrl || isHttpsUrl

private val String.isHttpsUrl get() = httpsValidator.isValid(this.trim())

