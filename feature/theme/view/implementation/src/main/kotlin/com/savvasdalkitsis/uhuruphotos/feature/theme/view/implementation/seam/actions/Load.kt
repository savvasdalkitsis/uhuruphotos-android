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
package com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.ThemeActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.ThemeMutation.SetTheme
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.ui.state.ThemeState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.theme.state.ThemeSettingsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal data object Load : ThemeAction() {
    override fun ThemeActionsContext.handle(state: ThemeState) : Flow<Mutation<ThemeState>> = with(settingsUIUseCase) {
        setThemeMode(themeModeDefault)
        setThemeVariant(themeVariantDefault)
        setThemeContrast(themeContrastDefault)
        setCollageSpacing(collageSpacingDefault)
        setCollageSpacingIncludeEdges(collageSpacingIncludeEdgesDefault)
        setCollageShape(collageShapeDefault)
        return combine(
            combine(
                observeThemeMode(),
                observeThemeVariant(),
                observeThemeContrast(),
            ) { mode, variant, contrast ->
                Triple(mode, variant, contrast)
            }, combine(
                observeCollageSpacing(),
                observeCollageSpacingIncludeEdges(),
                observeCollageShape(),
            ) { spacing, spacingEdges, shape ->
                Triple(spacing, spacingEdges, shape)
            }
        ) { (themeMode, themeVariant, themeContrast), (spacing, spacingEdges, shape) ->
            SetTheme(
                ThemeSettingsState(
                    themeMode = themeMode,
                    theme = themeVariant,
                    themeContrast = themeContrast,
                    collageSpacing = spacing,
                    collageSpacingIncludeEdges = spacingEdges,
                    collageShape = shape,
                )
            )
        }
    }
}