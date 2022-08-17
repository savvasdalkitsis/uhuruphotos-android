package com.savvasdalkitsis.uhuruphotos.implementation.media;

import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaId;
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItem;
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItemSelectionMode;
import com.savvasdalkitsis.uhuruphotos.api.media.remote.model.RemoteMediaItemSummary;

public class TestMediaItems {

    private TestMediaItems() {
        // not to be instantiated
    }

    public static final RemoteMediaItemSummary remoteMediaItemSummary = new RemoteMediaItemSummary(
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
