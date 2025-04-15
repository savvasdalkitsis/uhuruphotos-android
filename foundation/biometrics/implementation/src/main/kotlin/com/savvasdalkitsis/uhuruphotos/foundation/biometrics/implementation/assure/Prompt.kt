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
package com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure

import android.security.keystore.KeyGenParameterSpec
import androidx.annotation.CheckResult
import androidx.biometric.BiometricPrompt.PromptInfo

/**
 * Provides settings for biometric prompts.
 *
 * @param title Set the title to display.
 * @param subtitle Set the subtitle to display.
 * @param description Set the description to display.
 * @param negativeButtonText Set the text for the negative button. This would typically be used as
 *    a "Cancel" button, but may be also used to show an alternative method for authentication,
 *    such as screen that asks for a backup password.
 * @param confirmRequired Only applies to Q and above. A hint to the system to require user
 *    confirmation after a biometric has been authenticated. For example, implicit modalities like
 *    Face and Iris authentication are passive, meaning they don't require an explicit user action
 *    to complete. When set to 'false', the user action (e.g. pressing a button) will not be
 *    required. BiometricPrompt will require confirmation by default.
 * @param deviceCredentialsAllowed The user will first be prompted to authenticate with biometrics,
 *    but also given the option to authenticate with their device PIN, pattern, or password.
 *    Developers should first check [android.app.KeyguardManager.isDeviceSecure()] before enabling.
 * @param validityDurationSeconds Sets the duration of time (seconds) for which this key is
 *    authorized to be used after the user is successfully authenticated. This has effect if the
 *    key requires user authentication for its use. By default, if user authentication is required,
 *    it must take place for every use of the key.
 *
 * @author Aidan Follestad (@afollestad)
 */
data class Prompt(
  val title: String,
  val subtitle: String? = null,
  val description: String? = null,
  val negativeButtonText: String? = null,
  val confirmRequired: Boolean = false,
  val deviceCredentialsAllowed: Boolean = false,
  val validityDurationSeconds: Int? = null
) {
  @CheckResult
  internal fun toPromptInfo() = PromptInfo.Builder().apply {
    setTitle(title)
    setConfirmationRequired(confirmRequired)
    setDeviceCredentialAllowed(deviceCredentialsAllowed)
    subtitle?.let { setSubtitle(it) }
    description?.let { setDescription(it) }
    negativeButtonText?.let { setNegativeButtonText(it) }
  }.build()

  internal fun applyToKeyGenSpec(spec: KeyGenParameterSpec.Builder) {
    validityDurationSeconds?.let { spec.setUserAuthenticationValidityDurationSeconds(it) }
  }
}
