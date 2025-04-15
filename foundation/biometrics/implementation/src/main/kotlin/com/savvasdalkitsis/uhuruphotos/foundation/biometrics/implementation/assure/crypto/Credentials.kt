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

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import androidx.annotation.CheckResult
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.util.use

/**
 * Converts the [Credentials] to a [ByteArray], to be saved securely.
 *
 * @author Aidan Follestad (@afollestad)
 */
@SuppressLint("Recycle")
@CheckResult
fun Credentials.serialize(): ByteArray {
  return Parcel.obtain()
      .use {
        writeToParcel(this, 0)
        marshall()
      }
}

/**
 * Converts a [ByteArray] to a [Credentials].
 *
 * @author Aidan Follestad (@afollestad)
 */
@SuppressLint("Recycle")
@CheckResult
fun Credentials.Companion.deserialize(
  data: ByteArray
): Credentials {
  return Parcel.obtain()
      .use {
        unmarshall(data, 0, data.size)
        setDataPosition(0)
        CREATOR.createFromParcel(this)
      }
}

/**
 * Holds encryption settings. The [iv] is populated after
 * authenticating for encryption. It is required for decryption.
 * The [key] is always required, and is specified by you.
 *
 * @author Aidan Follestad (@afollestad)
 */
data class Credentials(
  val key: String,
  var iv: ByteArray = ByteArray(0)
) : Parcelable {
  constructor(parcel: Parcel) : this(
      parcel.readString() ?: error("Couldn't get key from Parcel"),
      parcel.createByteArray() ?: error("Couldn't get IV from Parcel")
  )

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as Credentials
    if (key != other.key) return false
    if (!iv.contentEquals(other.iv)) return false
    return true
  }

  override fun writeToParcel(
    dest: Parcel,
    flags: Int
  ) {
    dest.apply {
      writeString(key)
      writeByteArray(iv)
    }
  }

  override fun hashCode(): Int {
    var result = key.hashCode()
    result = 31 * result + iv.contentHashCode()
    return result
  }

  override fun describeContents(): Int = 0

  companion object {
    @JvmField
    val CREATOR = object : Creator<Credentials> {
      override fun createFromParcel(parcel: Parcel): Credentials {
        return Credentials(parcel)
      }

      override fun newArray(size: Int): Array<Credentials?> {
        return arrayOfNulls(size)
      }
    }
  }
}
