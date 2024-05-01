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
package com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.portfolio.PortfolioItems
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.domain.PortfolioItem
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.domain.PortfolioLocalMedia.Error
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.domain.PortfolioLocalMedia.Found
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.domain.PortfolioLocalMedia.RequiresPermissions
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.usecase.PortfolioUseCase
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.implementation.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class PortfolioUseCase(
    private val mediaUseCase: MediaUseCase,
    private val portfolioRepository: PortfolioRepository,
) : PortfolioUseCase {

    override fun observePortfolio() = combine(
        mediaUseCase.observeLocalMedia(),
        portfolioRepository.observePublishedPortfolio(),
    ) { media, published ->
        when (media) {
            is MediaItemsOnDevice.Found -> Found(
                listOfNotNull(media.primaryFolder
                    ?.toPortfolioItem(
                        isPublished = true,
                        canBeModified = false,
                    )
                ) +
                media.mediaFolders.map {
                    it.toPortfolioItem(
                        isPublished = it.first.id in published,
                        canBeModified = true,
                    )
                }
            )
            is MediaItemsOnDevice.RequiresPermissions -> RequiresPermissions(media.deniedPermissions)
            is MediaItemsOnDevice.Error -> Error
        }
    }

    override fun observePublishedFolderIds(): Flow<Set<Int>> =
        portfolioRepository.observePublishedPortfolio()

    override fun getPublishedFolderIds(): Set<Int> =
        portfolioRepository.getPublishedPortfolio()

    override fun setPortfolioFolderPublished(folderId: Int, published: Boolean) {
        portfolioRepository.setFolderPublished(folderId, published)
    }

    override fun publishItemToPortfolio(id: Long, folderId: Int, contribute: Boolean) {
        portfolioRepository.publishItemToPortfolio(id, folderId, contribute)
    }

    override fun observeIndividualPortfolioItems(): Flow<List<PortfolioItems>> =
        portfolioRepository.observeIndividualPortfolioItems()

    private fun Pair<LocalMediaFolder, List<MediaItem>>.toPortfolioItem(
        isPublished: Boolean,
        canBeModified: Boolean,
    ) = PortfolioItem(
        published = isPublished,
        canBeModified = canBeModified,
        folder = first,
        items = second,
    )
}
