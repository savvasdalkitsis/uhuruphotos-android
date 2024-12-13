package com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.ThemeActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.ThemeMutation.SetTheme
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.ui.state.ThemeState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.theme.state.ThemeSettingsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart

internal data object Load : ThemeAction() {
    override fun ThemeActionsContext.handle(state: ThemeState) : Flow<Mutation<ThemeState>> = with(settingsUIUseCase) {
        combine(
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
        }.onStart {
            setThemeMode(themeModeDefault)
            setThemeVariant(themeVariantDefault)
            setThemeContrast(themeContrastDefault)
            setCollageSpacing(collageSpacingDefault)
            setCollageSpacingIncludeEdges(collageSpacingIncludeEdgesDefault)
            setCollageShape(collageShapeDefault)
        }
    }
}