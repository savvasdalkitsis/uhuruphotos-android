package com.savvasdalkitsis.uhuruphotos.albums;

import com.savvasdalkitsis.uhuruphotos.albums.api.model.Album;
import com.savvasdalkitsis.uhuruphotos.albums.api.service.model.Album.CompleteAlbum;
import com.savvasdalkitsis.uhuruphotos.albums.api.service.model.Album.IncompleteAlbum;
import com.savvasdalkitsis.uhuruphotos.db.albums.Albums;

import java.util.ArrayList;
import java.util.Collections;

public class TestAlbums {

    private TestAlbums() {
        // not to be instantiated
    }

    public static final Album album = new Album(
            "id",
            Collections.emptyList(),
            "",
            ""
    );

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