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
package com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.implementation.repository

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.asFlowList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.asFlowSet
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.portfolio.PortfolioItems
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.portfolio.PortfolioItemsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.portfolio.PortfolioQueries
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PortfolioRepository @Inject constructor(
    private val portfolioQueries: PortfolioQueries,
    private val portfolioItemsQueries: PortfolioItemsQueries,
) {

    fun observePublishedPortfolio(): Flow<Set<Int>> = portfolioQueries.all()
        .asFlowSet()

    fun getPublishedPortfolio(): Set<Int> = portfolioQueries.all().executeAsList().toSet()

    fun publishItemToPortfolio(id: Long, folderId: Int, contribute: Boolean) {
        if (contribute) {
            portfolioItemsQueries.insert(id, folderId)
        } else {
            portfolioItemsQueries.remove(id)
        }
    }

    fun observeIndividualPortfolioItems(): Flow<List<PortfolioItems>> =
        portfolioItemsQueries.all().asFlowList()

    fun setFolderPublished(id: Int, published: Boolean) {
        if (published) {
            portfolioQueries.insert(id)
        } else {
            portfolioQueries.remove(id)
        }
    }
}
