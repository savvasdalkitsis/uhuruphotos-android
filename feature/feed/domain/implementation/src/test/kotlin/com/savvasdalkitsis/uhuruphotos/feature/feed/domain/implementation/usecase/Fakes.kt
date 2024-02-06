/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemGroup
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstance
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder

fun mediaCollection(
    id: String,
    vararg items: MediaId<*>,
) = mediaCollection(id, id, *items)

fun mediaCollection(
    id: String,
    date: String = id,
    vararg items: MediaId<*>,
) = MediaCollection(id, items.map { mediaItem(it, date) }, date)

fun mediaGroup(remote: MediaItemInstance, vararg locals: MediaItemInstance) = MediaItemGroup(remote, locals.toSet())
fun mediaItem(id: MediaId<*>, date: String = "") = MediaItemInstance(id, MediaItemHash(id.value.toString()), displayDayDate = date)
fun localMediaItem(id: Long, displayDate: String) = mediaItem(local(id), displayDate)
fun remote(id: String) = MediaId.Remote(id, false, "")
fun downloading(id: String) = MediaId.Downloading(id, false, "")
fun local(id: Long) = MediaId.Local(id, false, "", "")
fun processing(id: Long) = MediaId.Processing(id, false, "", "")
fun uploading(id: Long) = MediaId.Uploading(id, false, "", "")

fun mediaItem(id: String, hash: String = id) = GetRemoteMediaCollections(
    id,
    null,
    null,
    hash,
    null,
    null,
    null,
    null,
    null,
    null,
)

fun localFolder(id: Int) = LocalMediaFolder(id, id.toString())