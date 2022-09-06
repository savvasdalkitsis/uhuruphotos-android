package com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation;

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.person.GetPersonAlbums;

public class TestGetPersonAlbums {

    private TestGetPersonAlbums() {
        // not to be instantiated
    }

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