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
package com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.result

import androidx.annotation.CheckResult
import androidx.biometric.BiometricPrompt as BiometricConstants
/**
 * Provides error codes for biometric prompt results.
 *
 * @author Aidan Follestad (@afollestad)
 */
enum class BiometricError(
  internal val raw: Int
) {
  /**
   * The hardware is unavailable. Try again later.
   */
  ERROR_HW_UNAVAILABLE(BiometricConstants.ERROR_HW_UNAVAILABLE),
  /**
   * Error state returned when the sensor was unable to process the current image.
   */
  ERROR_UNABLE_TO_PROCESS(BiometricConstants.ERROR_UNABLE_TO_PROCESS),
  /**
   * Error state returned when the current request has been running too long. This is intended to
   * prevent programs from waiting for the biometric sensor indefinitely. The timeout is platform
   * and sensor-specific, but is generally on the order of 30 seconds.
   */
  ERROR_TIMEOUT(BiometricConstants.ERROR_TIMEOUT),
  /**
   * Error state returned for operations like enrollment; the operation cannot be completed
   * because there's not enough storage remaining to complete the operation.
   */
  ERROR_NO_SPACE(BiometricConstants.ERROR_NO_SPACE),
  /**
   * The operation was canceled because the biometric sensor is unavailable. For example, this may
   * happen when the user is switched, the device is locked or another pending operation prevents
   * or disables it.
   */
  ERROR_CANCELED(BiometricConstants.ERROR_CANCELED),
  /**
   * The operation was canceled because the API is locked out due to too many attempts.
   * This occurs after 5 failed attempts, and lasts for 30 seconds.
   */
  ERROR_LOCKOUT(BiometricConstants.ERROR_LOCKOUT),
  /**
   * Hardware vendors may extend this list if there are conditions that do not fall under one of
   * the above categories. Vendors are responsible for providing error strings for these errors.
   * These messages are typically reserved for internal operations such as enrollment, but may be
   * used to express vendor errors not otherwise covered. Applications are expected to show the
   * error message string if they happen, but are advised not to rely on the message id since they
   * will be device and vendor-specific
   */
  ERROR_VENDOR(BiometricConstants.ERROR_VENDOR),
  /**
   * The operation was canceled because ERROR_LOCKOUT occurred too many times.
   * Biometric authentication is disabled until the user unlocks with strong authentication
   * (PIN/Pattern/Password)
   */
  ERROR_LOCKOUT_PERMANENT(BiometricConstants.ERROR_LOCKOUT_PERMANENT),
  /**
   * The user canceled the operation. Upon receiving this, applications should use alternate
   * authentication (e.g. a password). The application should also provide the means to return to
   * biometric authentication, such as a "use <biometric>" button.
   */
  ERROR_USER_CANCELED(BiometricConstants.ERROR_USER_CANCELED),
  /**
   * The user does not have any biometrics enrolled.
   */
  ERROR_NO_BIOMETRICS(BiometricConstants.ERROR_NO_BIOMETRICS),
  /**
   * The device does not have a biometric sensor.
   */
  ERROR_HW_NOT_PRESENT(BiometricConstants.ERROR_HW_NOT_PRESENT),
  /**
   * The user pressed the negative button.
   */
  ERROR_NEGATIVE_BUTTON(BiometricConstants.ERROR_NEGATIVE_BUTTON),
  /**
   * The device does not have pin, pattern, or password set up.
   */
  ERROR_NO_DEVICE_CREDENTIAL(BiometricConstants.ERROR_NO_DEVICE_CREDENTIAL),
  /**
   * An unknown error occurred.
   */
  UNKNOWN(-1),
  /**
   * No error occurred. A default value.
   */
  NONE(0)
}

@CheckResult
internal fun Int.toBiometricError() = BiometricError.values().single { it.raw == this }
