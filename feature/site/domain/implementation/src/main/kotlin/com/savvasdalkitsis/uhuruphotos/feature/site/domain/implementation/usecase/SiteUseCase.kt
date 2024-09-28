/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.site.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.site.domain.api.model.SiteOptionsModel
import com.savvasdalkitsis.uhuruphotos.feature.site.domain.api.usecase.SiteUseCase
import com.savvasdalkitsis.uhuruphotos.feature.site.domain.implementation.service.http.SiteService
import javax.inject.Inject

class SiteUseCase @Inject constructor(
    private val siteService: SiteService,
) : SiteUseCase {

    override suspend fun getSiteOptions(): Result<SiteOptionsModel> = runCatching {
        val siteSettings = siteService.getSiteSettings()
        SiteOptionsModel(allowUpload = siteSettings.allowUpload)
    }
}