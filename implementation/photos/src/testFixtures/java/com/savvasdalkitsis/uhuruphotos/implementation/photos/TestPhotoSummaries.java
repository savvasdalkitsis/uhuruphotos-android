package com.savvasdalkitsis.uhuruphotos.implementation.photos;

import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoSummary;

public class TestPhotoSummaries {

    private TestPhotoSummaries() {
        // not to be instantiated
    }

    public static final PhotoSummary photoSummary = new PhotoSummary(
            "id",
            null,
            "url",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "containerId"
        );
}
