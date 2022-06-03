package com.savvasdalkitsis.uhuruphotos.photos;

import com.savvasdalkitsis.uhuruphotos.api.photos.model.Photo;
import com.savvasdalkitsis.uhuruphotos.api.photos.model.SelectionMode;
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSummaryItem;

public class TestPhotos {

    private TestPhotos() {
        // not to be instantiated
    }

    public static final PhotoSummaryItem photoSummaryItem = new PhotoSummaryItem(
            "id",
            "",
            "",
            "",
            null,
            "",
            1f,
            "",
            "",
            0
    );

    public static final Photo photo = new Photo(
            "id",
            "",
            "",
            null,
            false,
            1f,
            false,
            SelectionMode.UNDEFINED,
            null
    );
}
