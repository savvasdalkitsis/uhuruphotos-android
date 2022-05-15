package com.savvasdalkitsis.uhuruphotos.photos.model

sealed class PhotoSequenceDataSource {
    object Single : PhotoSequenceDataSource()
    object AllPhotos : PhotoSequenceDataSource()
}
