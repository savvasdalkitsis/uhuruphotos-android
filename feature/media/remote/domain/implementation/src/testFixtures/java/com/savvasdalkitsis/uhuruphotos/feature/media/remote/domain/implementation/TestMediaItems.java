/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation;

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId;
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem;
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSelectionMode;
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaItemSummary;

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
