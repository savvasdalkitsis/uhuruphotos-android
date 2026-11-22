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
package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.RemoteIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class HeatMapMutationTest {

    private val hash = MediaItemHashModel.fromRemoteMediaHash("hash", 1)
    private val media = listOf(
        MediaItemInstanceModel(RemoteIdModel("1", false, hash), hash, latLng = 52.5 to 13.4),
        MediaItemInstanceModel(RemoteIdModel("2", false, hash), hash),
    )

    @Test
    fun `keeps a single copy of the points for the whole and the visible map`() {
        val state = HeatMapMutation.UpdateAllMedia(media).reduce(HeatMapState())

        assertEquals(listOf(LatLon(52.5, 13.4)), state.allPoints)
        assertSame(state.allPoints, state.pointsOnVisibleMap)
    }
}
