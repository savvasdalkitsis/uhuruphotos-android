package com.savvasdalkitsis.uhuruphotos.implementation.media;

import com.savvasdalkitsis.uhuruphotos.api.db.media.MediaItemSummary;

public class TestMediaItemDbSummaries {

    private TestMediaItemDbSummaries() {
        // not to be instantiated
    }

    public static final MediaItemSummary photoSummary = new MediaItemSummary(
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
