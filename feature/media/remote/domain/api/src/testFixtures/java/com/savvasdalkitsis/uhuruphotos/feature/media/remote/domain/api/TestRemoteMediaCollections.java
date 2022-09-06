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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api;

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetRemoteMediaCollections;
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollections;
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollection.Complete;
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollection.Incomplete;

import java.util.ArrayList;

public class TestRemoteMediaCollections {

    private TestRemoteMediaCollections() {
        // not to be instantiated
    }

    public static final GetRemoteMediaCollections getRemoteMediaCollections = new GetRemoteMediaCollections(
            "albumId",
            "",
            null,
            "photoId",
            null,
            null,
            null,
            null
    );

    public static final RemoteMediaCollections remoteMediaCollections = new RemoteMediaCollections(
            "id",
            null,
            null,
            1,
            false,
            0
    );

    public static final Incomplete incompleteRemoteMediaCollection = new Incomplete(
            "id",
            "",
            "",
            true,
            0
    );

    public static final Complete completeRemoteMediaCollection = new Complete(
            "id",
            "",
            "",
            false,
            0,
            0,
            new ArrayList<>()
    );
}