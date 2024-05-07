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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state

import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import dev.icerock.moko.resources.ImageResource

enum class CatalogueSorting(val icon: ImageResource) {

    DATE_DESC(images.ic_sort_date_descending),
    DATE_ASC(images.ic_sort_date_ascending),
    ALPHABETICAL_ASC(images.ic_sort_az_ascending),
    ALPHABETICAL_DESC(images.ic_sort_az_descending);

    companion object {
        val default = DATE_DESC

        fun <T> List<T>.sorted(
            sorting: CatalogueSorting,
            timeStamp: (T) -> String?,
            title: (T) -> String?,
        ): List<T> = sortedBy {
            when (sorting) {
                DATE_DESC, DATE_ASC -> timeStamp(it)
                ALPHABETICAL_ASC, ALPHABETICAL_DESC -> title(it)
            }
        }.let {
            when (sorting) {
                DATE_ASC, ALPHABETICAL_ASC -> it
                DATE_DESC, ALPHABETICAL_DESC -> it.reversed()
            }
        }
    }
}