/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.api.mediastore.usecase

import android.content.ContentUris
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.provider.MediaStore.Video

object MediaStoreContentUriResolver {

    fun getContentUriForItem(id: Long, video: Boolean): Uri {
        val baseUri = when {
            video -> if (SDK_INT >= Q) {
                Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                Video.Media.EXTERNAL_CONTENT_URI
            }
            else -> if (SDK_INT >= Q) {
                Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                Images.Media.EXTERNAL_CONTENT_URI
            }
        }
        return ContentUris.withAppendedId(baseUri, id)
    }

}