package com.savvasdalkitsis.uhuruphotos.albums;

import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetAlbums;
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetPersonAlbums;

public class TestGetAlbums {

    private TestGetAlbums() {
        // not to be instantiated
    }

    public static final GetAlbums getAlbum = new GetAlbums(
        "albumId",
        null,
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