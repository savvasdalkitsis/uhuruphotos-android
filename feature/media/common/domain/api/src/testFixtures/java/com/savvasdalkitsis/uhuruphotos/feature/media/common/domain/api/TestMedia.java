package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api;

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection;
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId;
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstance;

import java.util.Collections;

public class TestMedia {

    private TestMedia() {}

    public static final MediaCollection mediaCollection = new MediaCollection(
            "id",
            Collections.emptyList(),
            "",
            null,
            ""
    );

    public static final MediaItemInstance mediaItem = new MediaItemInstance(
            new MediaId.Remote("id", false, "serverUrl"),
            "mediaHash",
            "",
            "",
            null,
            false,
            1f,
            null
        );
}
