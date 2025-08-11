package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model

import android.os.Parcelable
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaStoreContentUriResolver
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class FeedUri(val value: String) : Parcelable {
    fun resolve(
        md5sum: Md5Hash,
        serverUrl: String?,
        userId: Int?,
        isVideo: Boolean,
    ): String = when {
        value.startsWith(LOCAL_PREFIX) -> {
            val id = value.removePrefix(LOCAL_PREFIX).toLongOrNull()
            if (id != null) {
                MediaStoreContentUriResolver.getContentUriForItem(id, isVideo).toString()
            } else {
                ""
            }
        }
        value.startsWith(REMOTE_PREFIX) -> when {
            serverUrl != null && userId != null ->
                MediaItemHashModel(md5sum, userId).hash.toThumbnailUrlFromId(serverUrl)
            else -> ""
        }
        else -> ""
    }

    companion object {
        private const val LOCAL_PREFIX = "L:"
        private const val REMOTE_PREFIX = "R:"

        fun local(contentUri: String) = FeedUri("$LOCAL_PREFIX${contentUri.substringAfterLast("/")}")
        fun remote() = FeedUri(REMOTE_PREFIX)

    }
}