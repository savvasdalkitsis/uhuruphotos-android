package com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.denormalization

enum class DenormalizationType {
    NEW_LOCAL_MEDIA_FOUND,
    NEW_REMOTE_MEDIA_FOUND,
    FAVORITE_ADDED,
    FAVORITE_REMOVED,
    UPLOADING_LOCAL_MEDIA,
    UPLOADING_LOCAL_MEDIA_SUCCEEDED,
    UPLOADING_LOCAL_MEDIA_FAILED,
}