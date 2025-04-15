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

package com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto

import android.util.Base64
import androidx.annotation.CheckResult
import androidx.biometric.BiometricPrompt
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.BiometricErrorException
import java.nio.charset.Charset

typealias OnDecryptor = Decryptor.(error: BiometricErrorException?) -> Unit

/**
 * A class that can decrypt data.
 *
 * @author Aidan Follestad (@afollestad)
 */
interface Decryptor {
  /** Encrypts a [ByteArray] and returns the result. */
  @CheckResult
  fun decrypt(data: ByteArray): ByteArray

  /** Decrypts an encrypted [ByteArray] and returns the result. */
  @CheckResult
  fun decryptToString(
    data: ByteArray,
    charset: Charset = Charset.defaultCharset()
  ): String {
    return decrypt(data).toString(charset)
  }

  /** Decrypts a Base64 [String] to an original [String]. */
  @CheckResult
  fun decryptToString(
    data: String,
    charset: Charset = Charset.defaultCharset()
  ): String {
    val rawData: ByteArray = Base64.decode(data, Base64.DEFAULT)
    return decrypt(rawData).toString(charset)
  }
}

internal class ErrorDecryptor internal constructor(
  private val biometricErrorException: BiometricErrorException
) : Decryptor {
  override fun decrypt(data: ByteArray): ByteArray {
    throw biometricErrorException
  }
}

internal class RealDecryptor internal constructor(
  private val cryptoObject: BiometricPrompt.CryptoObject? = null
) : Decryptor {
  override fun decrypt(data: ByteArray): ByteArray {
    return cryptoObject?.cipher?.doFinal(data) ?: error("Cipher is null!")
  }
}
