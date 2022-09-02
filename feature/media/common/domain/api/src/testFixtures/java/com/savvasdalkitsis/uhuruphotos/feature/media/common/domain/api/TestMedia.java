package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api;

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection;
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId;
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem;
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSelectionMode;

import java.util.Collections;

public class TestMedia {

    private TestMedia() {}

    public static MediaCollection mediaCollection = new MediaCollection(
            "id",
            Collections.emptyList(),
            "",
            null,
            ""
    );

    public static final MediaItem mediaItem = new MediaItem(
            MediaId.Companion.invoke("id"),
            "mediaHash",
            "",
            "",
            null,
            null,
            null,
            false,
            1f,
            false,
            MediaItemSelectionMode.UNDEFINED,
            null
    );
}
