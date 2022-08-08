package com.savvasdalkitsis.uhuruphotos.implementation.media;

import com.savvasdalkitsis.uhuruphotos.api.db.media.RemoteMediaItemSummary;

public class TestMediaItemDbSummaries {

    private TestMediaItemDbSummaries() {
        // not to be instantiated
    }

    public static final RemoteMediaItemSummary dbRemoteMediaItemSummary = new RemoteMediaItemSummary(
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
