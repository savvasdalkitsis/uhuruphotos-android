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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state

data class VitrineState(
    val cel1: CelState? = null,
    val cel2: CelState? = null,
    val cel3: CelState? = null,
    val cel4: CelState? = null,
) {
    val hasMoreThanOneItem = cel2 != null
    val isEmpty = cel1 == null && cel2 == null && cel3 == null && cel4 == null

    companion object {
        operator fun invoke(celStates: List<CelState?>)= VitrineState(
            celStates.getOrNull(0),
            celStates.getOrNull(1),
            celStates.getOrNull(2),
            celStates.getOrNull(3),
        )
    }
}