/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")

package com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure

import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.result.BiometricError

/** @author Aidan Follestad (@afollestad) */
class BiometricErrorException internal constructor(
  val biometricError: BiometricError,
  errorMessage: String
) : Exception(biometricError.getExceptionMessage(errorMessage))

private fun BiometricError.getExceptionMessage(
  errorMessage: String
): String = if (errorMessage.isNotEmpty()) {
  "$name: $errorMessage"
} else {
  name
}
