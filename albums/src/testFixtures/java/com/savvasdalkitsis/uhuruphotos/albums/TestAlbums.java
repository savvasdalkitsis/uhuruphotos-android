package com.savvasdalkitsis.uhuruphotos.albums;

import com.savvasdalkitsis.uhuruphotos.albums.service.model.Album;
import com.savvasdalkitsis.uhuruphotos.db.albums.Albums;
import com.savvasdalkitsis.uhuruphotos.photos.service.model.PhotoSummaryItem;

import java.util.ArrayList;

public class TestAlbums {

    public static Albums album = new Albums(
            "id",
            null,
            null,
            1,
            false,
            0
    );

    public static Album.IncompleteAlbum incompleteAlbum = new Album.IncompleteAlbum(
            "id",
            "",
            "",
            true,
            0
    );

    public static Album.CompleteAlbum completeAlbum = new Album.CompleteAlbum(
            "id",
            "",
            "",
            false,
            0,
            0,
            new ArrayList<>()
    );
}