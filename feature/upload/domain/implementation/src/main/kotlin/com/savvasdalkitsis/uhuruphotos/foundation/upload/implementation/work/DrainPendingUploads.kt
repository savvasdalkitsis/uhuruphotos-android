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
package com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.work

import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem

internal suspend fun drainPendingUploads(
    pendingItems: suspend () -> List<UploadItem>,
    markAsUploading: suspend (List<UploadItem>) -> Unit,
    upload: suspend (item: UploadItem, position: Int, total: Int) -> Boolean,
): Set<UploadItem> {
    val attempted = mutableSetOf<Long>()
    val failed = mutableSetOf<UploadItem>()
    while (true) {
        val items = pendingItems().filterNot { it.id in attempted }
        if (items.isEmpty()) {
            return failed
        }
        markAsUploading(items)
        for ((index, item) in items.withIndex()) {
            attempted += item.id
            if (!upload(item, index + 1, items.size)) {
                failed += item
            }
        }
    }
}
