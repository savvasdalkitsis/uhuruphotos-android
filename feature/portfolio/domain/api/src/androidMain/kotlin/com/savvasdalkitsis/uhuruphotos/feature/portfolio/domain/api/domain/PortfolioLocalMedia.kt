package com.savvasdalkitsis.uhuruphotos.feature.portfolio.domain.api.domain

sealed class PortfolioLocalMedia {

    data class Found(val items: List<PortfolioItem>) : PortfolioLocalMedia()

    data class RequiresPermissions(val deniedPermissions: List<String>) : PortfolioLocalMedia()

    data object Error : PortfolioLocalMedia()
}
