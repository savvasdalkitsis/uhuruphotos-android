/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class MediaItemHashModel(
    val md5: Md5Hash,
    val userId: Int?,
) : Parcelable {
    /**
     * The media md5 hash combined with the user id.
     */
    val hash get() = "${md5.value}${userId?.toString().orEmpty()}"

    companion object {
        fun fromRemoteMediaHash(hash: String, userId: Int?) = MediaItemHashModel(
            Md5Hash(hash.removeSuffix(userId?.toString().orEmpty())), userId,
        )
    }
}