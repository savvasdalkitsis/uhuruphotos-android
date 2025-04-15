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

typealias OnEncryptor = Encryptor.(error: BiometricErrorException?) -> Unit

/**
 * A class that can encrypt data.
 *
 * @author Aidan Follestad (@afollestad)
 */
interface Encryptor {
  /** Encrypts a [ByteArray] and returns the result. */
  @CheckResult
  fun encrypt(data: ByteArray): ByteArray

  /** Encrypts a [String] with a given [charset] and returns the result. */
  @CheckResult
  fun encrypt(
    data: String,
    charset: Charset = Charset.defaultCharset()
  ): ByteArray {
    return encrypt(data.toByteArray(charset))
  }

  /** Encrypts a [String] and returns a Base64 representation of the result. */
  @CheckResult
  fun encryptToString(
    data: String,
    charset: Charset = Charset.defaultCharset()
  ): String {
    return Base64.encodeToString(encrypt(data, charset), Base64.DEFAULT)
  }
}

internal class ErrorEncryptor internal constructor(
  private val biometricErrorException: BiometricErrorException
) : Encryptor {
  override fun encrypt(data: ByteArray): ByteArray {
    throw biometricErrorException
  }
}

internal class RealEncryptor internal constructor(
  private val cryptoObject: BiometricPrompt.CryptoObject? = null
) : Encryptor {
  override fun encrypt(data: ByteArray): ByteArray {
    return cryptoObject?.cipher?.doFinal(data) ?: error("Cipher is null!")
  }
}
