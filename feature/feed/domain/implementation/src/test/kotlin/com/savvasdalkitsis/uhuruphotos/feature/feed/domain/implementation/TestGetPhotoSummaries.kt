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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.TestMediaItemDbSummaries.dbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.TestMediaItems.remoteMediaItemSummary

fun mediaItemId(id: Int) = "mediaItem$id"
fun mediaItemSummary(id: Int) = dbRemoteMediaItemSummary.copy(id = mediaItemId(id))
fun mediaItemSummary(id: Int, inCollection: Int) = dbRemoteMediaItemSummary.copy(id = mediaItemId(id), containerId = collectionId(inCollection))
fun mediaItemSummaryItem(id: Int) = remoteMediaItemSummary.copy(id = mediaItemId(id))