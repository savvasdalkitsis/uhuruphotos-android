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

import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.Credentials
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.Crypto
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.CryptoMode
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.ErrorDecryptor
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.ErrorEncryptor
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.OnDecryptor
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.OnEncryptor
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.RealDecryptor
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.crypto.RealEncryptor
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.result.BiometricError.UNKNOWN
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.result.toBiometricError

internal fun BiometricPrompt.cancelAuthOnPause(
  lifecycleOwner: LifecycleOwner
): BiometricPrompt = apply {
  val observer = object : LifecycleObserver {
    @Suppress("unused")
    @OnLifecycleEvent(ON_PAUSE)
    fun onPause() {
      cancelAuthentication()
      lifecycleOwner.lifecycle.removeObserver(this)
    }
  }
  lifecycleOwner.lifecycle.addObserver(observer)
}

typealias OnAuthentication = (error: BiometricErrorException?) -> Unit

internal fun createAuthenticateCallback(
  callback: OnAuthentication
): BiometricPrompt.AuthenticationCallback {
  return object : BiometricPrompt.AuthenticationCallback() {
    override fun onAuthenticationError(
      errorCode: Int,
      errString: CharSequence
    ) {
      callback(BiometricErrorException(errorCode.toBiometricError(), errString.toString()))
    }

    override fun onAuthenticationFailed() {
      callback(BiometricErrorException(UNKNOWN, "Unknown failure"))
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
      callback(null)
    }
  }
}

internal fun createEncryptCallback(
  credentials: Credentials?,
  callback: OnEncryptor
): BiometricPrompt.AuthenticationCallback {
  return object : BiometricPrompt.AuthenticationCallback() {
    override fun onAuthenticationError(
      errorCode: Int,
      errString: CharSequence
    ) {
      val exception = BiometricErrorException(errorCode.toBiometricError(), errString.toString())
      callback(ErrorEncryptor(exception), exception)
    }

    override fun onAuthenticationFailed() {
      val exception = BiometricErrorException(UNKNOWN, "Unknown failure")
      callback(ErrorEncryptor(exception), exception)
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
      result.cryptoObject?.let {
        credentials?.iv = it.cipher?.iv ?: error("Unable to get IV")
      }
      callback(RealEncryptor(cryptoObject = result.cryptoObject), null)
    }
  }
}

internal fun createDecryptCallback(
  callback: OnDecryptor
): BiometricPrompt.AuthenticationCallback {
  return object : BiometricPrompt.AuthenticationCallback() {
    override fun onAuthenticationError(
      errorCode: Int,
      errString: CharSequence
    ) {
      val exception = BiometricErrorException(errorCode.toBiometricError(), errString.toString())
      callback(ErrorDecryptor(exception), exception)
    }

    override fun onAuthenticationFailed() {
      val exception = BiometricErrorException(UNKNOWN, "Unknown failure")
      callback(ErrorDecryptor(exception), exception)
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
      callback(RealDecryptor(cryptoObject = result.cryptoObject), null)
    }
  }
}

internal fun BiometricPrompt.authenticate(
  prompt: Prompt,
  mode: CryptoMode,
  credentials: Credentials? = null
) {
  val promptInfo: PromptInfo = prompt.toPromptInfo()
  val cryptoObject: CryptoObject? = credentials
      ?.let { Crypto(it) }
      ?.getObject(
          mode = mode,
          keyGenModifier = prompt::applyToKeyGenSpec
      )
  cryptoObject
      ?.let { authenticate(promptInfo, it) }
      ?: authenticate(promptInfo)
}
