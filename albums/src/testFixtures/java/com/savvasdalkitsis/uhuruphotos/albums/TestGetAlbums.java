package com.savvasdalkitsis.uhuruphotos.albums;

import com.savvasdalkitsis.uhuruphotos.db.albums.GetAlbums;
import com.savvasdalkitsis.uhuruphotos.db.albums.GetPersonAlbums;

public class TestGetAlbums {

    public static GetAlbums getAlbum = new GetAlbums(
        "albumId",
        null,
        null,
        "photoId",
        null,
        null,
        null,
        null
    );

    public static GetPersonAlbums getPersonAlbum = new GetPersonAlbums(
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