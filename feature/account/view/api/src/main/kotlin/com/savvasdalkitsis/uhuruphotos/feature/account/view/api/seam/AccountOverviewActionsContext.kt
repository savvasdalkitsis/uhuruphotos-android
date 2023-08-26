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
package com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam

import com.savvasdalkitsis.uhuruphotos.feature.account.domain.api.usecase.AccountUseCase
import com.savvasdalkitsis.uhuruphotos.feature.avatar.domain.api.usecase.AvatarUseCase
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.usecase.JobsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.usecase.SyncUseCase
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.usecase.UploadsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import javax.inject.Inject

class AccountOverviewActionsContext @Inject constructor(
    val accountUseCase: AccountUseCase,
    val avatarUseCase: AvatarUseCase,
    val jobsUseCase: JobsUseCase,
    val navigator: Navigator,
    val welcomeUseCase: WelcomeUseCase,
    val uploadsUseCase: UploadsUseCase,
    val syncUseCase: SyncUseCase,
)