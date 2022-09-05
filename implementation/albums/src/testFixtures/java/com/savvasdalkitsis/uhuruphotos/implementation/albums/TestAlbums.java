package com.savvasdalkitsis.uhuruphotos.implementation.albums;

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.albums.Albums;
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollection.Complete;
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollection.Incomplete;

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

    public static final Incomplete incompleteAlbum = new Incomplete(
            "id",
            "",
            "",
            true,
            0
    );

    public static final Complete completeAlbum = new Complete(
            "id",
            "",
            "",
            false,
            0,
            0,
            new ArrayList<>()
    );
}