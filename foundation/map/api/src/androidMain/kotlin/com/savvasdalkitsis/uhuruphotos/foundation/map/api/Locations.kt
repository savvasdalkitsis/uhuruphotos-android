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
package com.savvasdalkitsis.uhuruphotos.foundation.map.api

import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.Viewport
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.toLatLon

data object Locations {
    @Suppress("MagicNumber")
    val TRAFALGAR_SQUARE: Viewport = Viewport(
        (51.50803011165474 to -0.12805053251940438).toLatLon,
        3f
    )
}