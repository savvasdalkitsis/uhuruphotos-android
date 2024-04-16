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
package com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.ui

import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.maps.extension.style.StyleContract
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.layers.generated.heatmapLayer
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon

internal fun createHeatMapSource(
    layerId: String,
    points: Set<LatLon>,
) = GeoJsonSource.Builder(layerId)
    .featureCollection(
        FeatureCollection.fromFeatures(points
        .map { Feature.fromGeometry(it.toPoint) })
    )
    .build()

internal fun createHeatmapLayer(
    layerId: String,
    sourceId: String,
): StyleContract.StyleLayerExtension =
    heatmapLayer(
        layerId,
        sourceId,
    ) {
        maxZoom(9.0)
        sourceLayer(sourceId)
        heatmapColor(
            interpolate {
                linear()
                heatmapDensity()
                stop {
                    literal(0)
                    rgba(33.0, 102.0, 172.0, 0.0)
                }
                stop {
                    literal(0.2)
                    rgb(103.0, 169.0, 207.0)
                }
                stop {
                    literal(0.4)
                    rgb(209.0, 229.0, 240.0)
                }
                stop {
                    literal(0.6)
                    rgb(253.0, 219.0, 240.0)
                }
                stop {
                    literal(0.8)
                    rgb(239.0, 138.0, 98.0)
                }
                stop {
                    literal(1)
                    rgb(178.0, 24.0, 43.0)
                }
            }
        )
        heatmapWeight(
            interpolate {
                linear()
                get { literal("mag") }
                stop {
                    literal(0)
                    literal(0)
                }
                stop {
                    literal(6)
                    literal(1)
                }
            }
        )
        heatmapIntensity(
            interpolate {
                linear()
                zoom()
                stop {
                    literal(0)
                    literal(1)
                }
                stop {
                    literal(9)
                    literal(3)
                }
            }
        )
        heatmapRadius(
            interpolate {
                linear()
                zoom()
                stop {
                    literal(0)
                    literal(2)
                }
                stop {
                    literal(9)
                    literal(20)
                }
            }
        )
        heatmapOpacity(
            interpolate {
                linear()
                zoom()
                stop {
                    literal(7)
                    literal(1)
                }
                stop {
                    literal(9)
                    literal(0)
                }
            }
        )
    }

internal fun createHeatMapCircles(
    layerId: String,
    sourceId: String,
) =
    circleLayer(
        layerId,
        sourceId,
    ) {
        circleRadius(
            interpolate {
                linear()
                zoom()
                stop {
                    literal(7)
                    interpolate {
                        linear()
                        get { literal("mag") }
                        stop {
                            literal(1)
                            literal(1)
                        }
                        stop {
                            literal(6)
                            literal(4)
                        }
                    }
                }
                stop {
                    literal(16)
                    interpolate {
                        linear()
                        get { literal("mag") }
                        stop {
                            literal(1)
                            literal(5)
                        }
                        stop {
                            literal(6)
                            literal(50)
                        }
                    }
                }
            }
        )
        circleColor(
            interpolate {
                linear()
                get { literal("mag") }
                stop {
                    literal(1)
                    rgba(33.0, 102.0, 172.0, 0.0)
                }
                stop {
                    literal(2)
                    rgb(102.0, 169.0, 207.0)
                }
                stop {
                    literal(3)
                    rgb(209.0, 229.0, 240.0)
                }
                stop {
                    literal(4)
                    rgb(253.0, 219.0, 199.0)
                }
                stop {
                    literal(5)
                    rgb(239.0, 138.0, 98.0)
                }
                stop {
                    literal(6)
                    rgb(178.0, 24.0, 43.0)
                }
            }
        )
        circleOpacity(
            interpolate {
                linear()
                zoom()
                stop {
                    literal(7)
                    literal(0)
                }
                stop {
                    literal(8)
                    literal(1)
                }
            }
        )
        circleStrokeColor("white")
        circleStrokeWidth(0.1)
    }