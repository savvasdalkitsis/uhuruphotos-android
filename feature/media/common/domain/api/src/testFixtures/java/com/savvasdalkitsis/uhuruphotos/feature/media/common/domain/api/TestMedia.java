package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api;

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection;
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId;
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem;
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstance;
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState;

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

    public static final MediaItem mediaItem = new MediaItemInstance(
            new MediaId.Remote("id", false),
            "mediaHash",
            "",
            "",
            null,
            null,
            null,
            false,
            1f,
            null,
            MediaItemSyncState.REMOTE_ONLY
        );
}
