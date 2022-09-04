package com.savvasdalkitsis.uhuruphotos.implementation.albums;

import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.Album.CompleteAlbum;
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.Album.IncompleteAlbum;
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.albums.Albums;

import java.util.ArrayList;

public class TestAlbums {

    private TestAlbums() {
        // not to be instantiated
    }

    public static final Albums albums = new Albums(
            "id",
            null,
            null,
            1,
            false,
            0
    );

    public static final IncompleteAlbum incompleteAlbum = new IncompleteAlbum(
            "id",
            "",
            "",
            true,
            0
    );

    public static final CompleteAlbum completeAlbum = new CompleteAlbum(
            "id",
            "",
            "",
            false,
            0,
            0,
            new ArrayList<>()
    );
}