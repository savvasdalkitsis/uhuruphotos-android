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
package com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.module

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.module.DbModule
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.module.CommonMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.usecase.PortfolioUseCase
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.implementation.repository.PortfolioRepository

object PortfolioModule {

    val portfolioUseCase: PortfolioUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.implementation.usecase.PortfolioUseCase(
            CommonMediaModule.mediaUseCase,
            portfolioRepository,
        )

    private val portfolioRepository: PortfolioRepository
        get() = PortfolioRepository(
            DbModule.database.portfolioQueries,
            DbModule.database.portfolioItemsQueries,
        )
}