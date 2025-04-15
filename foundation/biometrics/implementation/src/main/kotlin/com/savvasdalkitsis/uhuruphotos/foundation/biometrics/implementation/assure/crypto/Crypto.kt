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
package com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.BLOCK_MODE_CBC
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_PKCS7
import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import androidx.annotation.CheckResult
import androidx.biometric.BiometricPrompt.CryptoObject
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.CryptoMode.DECRYPT
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.CryptoMode.ENCRYPT
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

private const val ANDROID_KEY_STORE = "AndroidKeyStore"

internal enum class CryptoMode {
  ENCRYPT,
  DECRYPT
}

internal class Crypto(
  private val credentials: Credentials
) {
  @CheckResult fun getObject(
    mode: CryptoMode,
    keyGenModifier: KeyGenParameterSpec.Builder.() -> Unit = {}
  ): CryptoObject? {
    return when (mode) {
      ENCRYPT -> getEncryptionObject(
          keyGenModifier = keyGenModifier
      )
      DECRYPT -> getDecryptionObject(
          iv = credentials.iv,
          keyGenModifier = keyGenModifier
      )
    }
  }

  private fun getEncryptionObject(
    keyGenModifier: KeyGenParameterSpec.Builder.() -> Unit = {}
  ): CryptoObject? {
    maybeGenerateSecretKey(keyGenModifier)
    val secretKey = getSecretKey()
    val cipher = getCipher().apply {
      init(ENCRYPT_MODE, secretKey)
    }
    return CryptoObject(cipher)
  }

  private fun getDecryptionObject(
    iv: ByteArray,
    keyGenModifier: KeyGenParameterSpec.Builder.() -> Unit = {}
  ): CryptoObject? {
    require(iv.isNotEmpty()) {
      "Cannot perform decryption with a null IV."
    }
    maybeGenerateSecretKey(keyGenModifier)
    val secretKey = getSecretKey()
    val cipher = getCipher().apply {
      init(DECRYPT_MODE, secretKey, IvParameterSpec(iv))
    }
    return CryptoObject(cipher)
  }

  private fun maybeGenerateSecretKey(
    keyGenModifier: KeyGenParameterSpec.Builder.() -> Unit
  ) {
    val existingKey = getSecretKey()
    if (existingKey != null) {
      // Don't need to generate again.
      return
    }
    val keyName = credentials.key
    val spec = KeyGenParameterSpec.Builder(keyName, PURPOSE_ENCRYPT or PURPOSE_DECRYPT)
        .setBlockModes(BLOCK_MODE_CBC)
        .setEncryptionPaddings(ENCRYPTION_PADDING_PKCS7)
        .setUserAuthenticationRequired(true)
        .apply(keyGenModifier)
        .build()
    KeyGenerator.getInstance(KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        .apply {
          init(spec)
          generateKey()
        }
  }

  private fun getSecretKey(): SecretKey? {
    val keyName = credentials.key
    return KeyStore.getInstance(ANDROID_KEY_STORE)
        .apply { load(null) }
        .getKey(keyName, null) as? SecretKey
  }

  private fun getCipher(): Cipher {
    return Cipher.getInstance(
        KEY_ALGORITHM_AES + "/" +
            BLOCK_MODE_CBC + "/" +
            ENCRYPTION_PADDING_PKCS7
    )
  }
}
