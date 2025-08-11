package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model

import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

object MediaStoreContentUriResolver {

    fun getContentUriForItem(id: Long, video: Boolean): Uri {
        val baseUri = when {
            video -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
            else -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        }
        return ContentUris.withAppendedId(baseUri, id)
    }

}