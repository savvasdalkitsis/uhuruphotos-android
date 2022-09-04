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
package com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.albums.GetAlbums
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.albums.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.albums.GetTrash
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.albums.GetUserAlbum
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.domain.model.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.search.GetSearchResults

val GetAlbums.isVideo get() = type.isVideo
val GetTrash.isVideo get() = type.isVideo
val GetPersonAlbums.isVideo get() = type.isVideo
val GetUserAlbum.isVideo get() = type.isVideo
val GetSearchResults.isVideo get() = type.isVideo
val DbRemoteMediaItemSummary.isVideo get() = type.isVideo
private val String?.isVideo get() = this == "video"