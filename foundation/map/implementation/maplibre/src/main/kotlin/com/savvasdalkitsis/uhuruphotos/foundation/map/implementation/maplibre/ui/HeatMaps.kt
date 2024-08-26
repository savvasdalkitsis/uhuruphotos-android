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
package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.maplibre.ui

import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import org.maplibre.android.style.expressions.Expression.get
import org.maplibre.android.style.expressions.Expression.heatmapDensity
import org.maplibre.android.style.expressions.Expression.interpolate
import org.maplibre.android.style.expressions.Expression.linear
import org.maplibre.android.style.expressions.Expression.literal
import org.maplibre.android.style.expressions.Expression.rgb
import org.maplibre.android.style.expressions.Expression.rgba
import org.maplibre.android.style.expressions.Expression.stop
import org.maplibre.android.style.expressions.Expression.zoom
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.HeatmapLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection

internal fun createHeatMapSource(
    layerId: String,
    points: Set<LatLon>,
) = GeoJsonSource(layerId)
    .apply {
        setGeoJson(
            FeatureCollection.fromFeatures(points
                .map { Feature.fromGeometry(it.toPoint) })
        )
    }


internal fun createHeatmapLayer(
    layerId: String,
    sourceId: String,
) = HeatmapLayer(layerId, sourceId).apply {
    maxZoom = 9f
    sourceLayer = sourceId
    setProperties(
        PropertyFactory.heatmapColor(
            interpolate(
                linear(), heatmapDensity(),
                literal(0), rgba(33, 102, 172, 0),
                literal(0.2), rgb(103, 169, 207),
                literal(0.4), rgb(209, 229, 240),
                literal(0.6), rgb(253, 219, 199),
                literal(0.8), rgb(239, 138, 98),
                literal(1), rgb(178, 24, 43)
            )
        ),
        PropertyFactory.heatmapWeight(
            interpolate(
                linear(), get("mag"),
                stop(0, 0),
                stop(6, 1)
            )
        ),
        PropertyFactory.heatmapIntensity(
            interpolate(
                linear(), zoom(),
                stop(0, 1),
                stop(9, 3)
            )
        ),
        PropertyFactory.heatmapRadius(
            interpolate(
                linear(), zoom(),
                stop(0, 2),
                stop(9, 20)
            )
        ),
        PropertyFactory.heatmapOpacity(
            interpolate(
                linear(), zoom(),
                stop(7, 1),
                stop(9, 0)
            )
        )
    )
}

internal fun createHeatMapCircles(
    layerId: String,
    sourceId: String,
) = CircleLayer(layerId, sourceId).apply {
    setProperties(
        PropertyFactory.circleRadius(
            interpolate(
                linear(), zoom(),
                literal(7), interpolate(
                    linear(), get("mag"),
                    stop(1, 1),
                    stop(6, 4)
                ),
                literal(16), interpolate(
                    linear(), get("mag"),
                    stop(1, 5),
                    stop(6, 50)
                )
            )
        ),
        PropertyFactory.circleColor(
            interpolate(
                linear(), get("mag"),
                literal(1), rgba(33, 102, 172, 0),
                literal(2), rgb(103, 169, 207),
                literal(3), rgb(209, 229, 240),
                literal(4), rgb(253, 219, 199),
                literal(5), rgb(239, 138, 98),
                literal(6), rgb(178, 24, 43)
            )
        ),
        PropertyFactory.circleOpacity(
            interpolate(
                linear(), zoom(),
                stop(7, 0),
                stop(8, 1)
            )
        ),
        PropertyFactory.circleStrokeColor("white"),
        PropertyFactory.circleStrokeWidth(1.0f)
    )
}
