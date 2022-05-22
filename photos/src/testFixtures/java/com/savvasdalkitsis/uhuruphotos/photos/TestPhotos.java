package com.savvasdalkitsis.uhuruphotos.photos;

import com.savvasdalkitsis.uhuruphotos.photos.api.model.Photo;
import com.savvasdalkitsis.uhuruphotos.photos.api.model.SelectionMode;
import com.savvasdalkitsis.uhuruphotos.photos.service.model.PhotoSummaryItem;

public class TestPhotos {

    public static PhotoSummaryItem photoSummaryItem = new PhotoSummaryItem(
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
