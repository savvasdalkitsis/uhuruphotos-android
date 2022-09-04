package com.savvasdalkitsis.uhuruphotos.implementation.albums;

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetRemoteMediaCollections;
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.person.GetPersonAlbums;

public class TestGetAlbums {

    private TestGetAlbums() {
        // not to be instantiated
    }

    public static final GetRemoteMediaCollections getRemoteMediaCollections = new GetRemoteMediaCollections(
        "albumId",
        "",
        null,
        "photoId",
        null,
        null,
        null,
        null
    );

    public static final GetPersonAlbums getPersonAlbum = new GetPersonAlbums(
            "albumId",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
            0
    );
}