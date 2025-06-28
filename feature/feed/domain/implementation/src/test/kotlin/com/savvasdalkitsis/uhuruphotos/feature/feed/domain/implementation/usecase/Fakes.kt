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
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemGroupModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash

fun mediaCollection(
    id: String,
    vararg items: MediaIdModel<*>,
) = mediaCollection(id, id, *items)

fun mediaCollection(
    id: String,
    date: String = id,
    vararg items: MediaIdModel<*>,
) = MediaCollectionModel(id, items.map { mediaItem(it, date) }, date)

fun mediaGroup(remote: MediaItemInstanceModel, vararg locals: MediaItemInstanceModel) = MediaItemGroupModel(remote, locals.toSet())
fun mediaItem(id: MediaIdModel<*>, date: String = "") =
    MediaItemInstanceModel(id, MediaItemHashModel.fromRemoteMediaHash(id.value.toString(), 0), displayDayDate = date)
fun localMediaItem(id: Long, displayDate: String) = mediaItem(local(id), displayDate)
fun remote(id: String) = MediaIdModel.RemoteIdModel(id, false, MediaItemHashModel.fromRemoteMediaHash(id, 0))
fun downloading(id: String) = MediaIdModel.DownloadingIdModel(id, false, MediaItemHashModel(Md5Hash(""), 0))
fun local(id: Long) = MediaIdModel.LocalIdModel(id, 0, false, "", "", MediaItemHashModel(Md5Hash(""), 0))
fun processing(id: Long) = MediaIdModel.ProcessingIdModel(id, 0, false, "", "", MediaItemHashModel(Md5Hash(""), 0))
fun uploading(id: Long) = MediaIdModel.UploadingIdModel(id, 0, false, "", "", MediaItemHashModel(Md5Hash(""), 0))

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