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
package com.savvasdalkitsis.uhuruphotos.implementation.home.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Seam
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.handler
import com.savvasdalkitsis.uhuruphotos.implementation.home.seam.HomeAction
import com.savvasdalkitsis.uhuruphotos.implementation.home.seam.HomeActionHandler
import com.savvasdalkitsis.uhuruphotos.implementation.home.seam.HomeEffect
import com.savvasdalkitsis.uhuruphotos.implementation.home.seam.HomeMutation
import com.savvasdalkitsis.uhuruphotos.implementation.home.view.state.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    homeActionHandler: HomeActionHandler,
) : ViewModel(),
    Seam<HomeState, HomeEffect, HomeAction, HomeMutation> by handler(
        homeActionHandler,
        HomeState()
    )