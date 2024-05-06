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
package com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioCelState
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioItems
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui.state.PortfolioState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.collections.immutable.toPersistentList

sealed class PortfolioMutation(
    mutation: Mutation<PortfolioState>,
) : Mutation<PortfolioState> by mutation {

    data class RequestPermissions(val deniedPermissions: List<String>): PortfolioMutation({
        it.copy(localMedia = PortfolioItems.RequiresPermissions(deniedPermissions))
    })

    data object ShowError : PortfolioMutation({
        it
    })

    data class DisplayPortfolio(val states: List<PortfolioCelState>, val scanningOther: Boolean) : PortfolioMutation({
        it.copy(localMedia = PortfolioItems.Found(states.toPersistentList()), showScanOther = !scanningOther)
    })

    data class ChangeTitle(val title: String) : PortfolioMutation({
        it.copy(title = title)
    })
}