package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api;


import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetPhotoSummariesForAlbum;

public class TestGetPhotoSummaries {

    private TestGetPhotoSummaries() {
        // not to be instantiated
    }

    public static final GetPhotoSummariesForAlbum photoSummariesForAlbum = new GetPhotoSummariesForAlbum(
            "photoSummariesForAlbumId",
            ""
    );
}
