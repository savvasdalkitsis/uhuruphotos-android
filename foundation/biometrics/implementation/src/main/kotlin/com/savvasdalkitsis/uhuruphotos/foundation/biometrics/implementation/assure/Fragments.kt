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
@file:Suppress("unused")

package com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.Credentials
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.CryptoMode.DECRYPT
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.CryptoMode.ENCRYPT
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.Decryptor
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.Encryptor
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.OnDecryptor
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.OnEncryptor

/**
 * Shows the biometric authentication prompt. Calls the [onAuthentication] callback when
 * authentication is successful, otherwise a [BiometricErrorException] is thrown with details.
 *
 * @param prompt Provides settings for the biometric prompt.
 * @param onAuthentication The callback to invoke with the result.
 *
 * @author Aidan Follestad (@afollestad)
 */
fun Fragment.authenticate(
  prompt: Prompt,
  onAuthentication: OnAuthentication
) {
  val activity = requireActivity()
  val executor = ContextCompat.getMainExecutor(activity)
  val callback = createAuthenticateCallback(onAuthentication)

  BiometricPrompt(this, executor, callback)
      .cancelAuthOnPause(this)
      .authenticate(
          prompt = prompt,
          mode = ENCRYPT
      )
}

/**
 * Shows the biometric authentication prompt, authenticating a key to encrypt data. Sends an
 * [Encryptor] through the [onAuthentication] callback when authentication is successful.
 * Otherwise a [BiometricErrorException] is thrown with details.
 *
 * @param credentials Encryption settings, the key is important here. The IV is populated after auth.
 * @param prompt Provides settings for the biometric prompt.
 * @param onAuthentication The callback to invoke with the result.
 *
 * @author Aidan Follestad (@afollestad)
 */
fun Fragment.authenticateForEncrypt(
  credentials: Credentials,
  prompt: Prompt,
  onAuthentication: OnEncryptor
) {
  val activity = requireActivity()
  val executor = ContextCompat.getMainExecutor(activity)
  val callback = createEncryptCallback(
      credentials = credentials,
      callback = onAuthentication
  )

  BiometricPrompt(this, executor, callback)
      .cancelAuthOnPause(this)
      .authenticate(
          prompt = prompt,
          mode = ENCRYPT,
          credentials = credentials
      )
}

/**
 * Shows the biometric authentication prompt, authenticating a key to decrypt data. Sends an
 * [Decryptor] through the [onAuthentication] callback when authentication is successful.
 * Otherwise a [BiometricErrorException] is thrown with details.
 *
 * @param credentials Decryption settings, both a key and populated IV are needed.
 * @param prompt Provides settings for the biometric prompt.
 * @param onAuthentication The callback to invoke with the result.
 *
 * @author Aidan Follestad (@afollestad)
 */
fun Fragment.authenticateForDecrypt(
  credentials: Credentials,
  prompt: Prompt,
  onAuthentication: OnDecryptor
) {
  val activity = requireActivity()
  val executor = ContextCompat.getMainExecutor(activity)
  val callback = createDecryptCallback(onAuthentication)

  BiometricPrompt(this, executor, callback)
      .authenticate(
          prompt = prompt,
          mode = DECRYPT,
          credentials = credentials
      )
}
