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
package com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.domain.PortfolioLocalMedia.Error
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.domain.PortfolioLocalMedia.Found
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.domain.PortfolioLocalMedia.RequiresPermissions
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.api.navigation.PortfolioNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.seam.PortfolioActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.seam.PortfolioMutation
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.seam.PortfolioMutation.RequestPermissions
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.seam.PortfolioMutation.ShowError
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioCelState
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SelectionMode.SELECTED
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SelectionMode.UNDEFINED
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SelectionMode.UNSELECTED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

data class Load(val route: PortfolioNavigationRoute) : PortfolioAction() {

    context(PortfolioActionsContext)
    override fun handle(state: PortfolioState): Flow<Mutation<PortfolioState>> = flow {
        emit(PortfolioMutation.ChangeTitle(route.title))
        emitAll(
            portfolioUseCase.observePortfolio().map { portfolio ->
                when(portfolio) {
                    is Found -> PortfolioMutation.DisplayPortfolio(portfolio.items.map { item ->
                        PortfolioCelState(
                            selection = when {
                                !route.editMode -> UNDEFINED
                                item.published -> SELECTED
                                else -> UNSELECTED
                            },
                            clickable = when {
                                route.editMode -> item.canBeModified
                                else -> true
                            },
                            folder = item.folder,
                            vitrine = VitrineState(item.items.map { it.toCel() }),
                        )
                    })
                    is RequiresPermissions -> RequestPermissions(portfolio.deniedPermissions)
                    Error -> ShowError
                }
            }
        )
    }
}