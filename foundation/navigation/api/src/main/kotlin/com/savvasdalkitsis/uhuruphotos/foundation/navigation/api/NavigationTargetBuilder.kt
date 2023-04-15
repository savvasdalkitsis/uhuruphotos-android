package com.savvasdalkitsis.uhuruphotos.foundation.navigation.api

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.HasActionableState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.HasInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.ThemeMode
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KClass

interface NavigationTargetBuilder {

    fun <S : Any, A : Any, VM, R : NavigationRoute> NavGraphBuilder.navigationTarget(
        themeMode: StateFlow<ThemeMode>,
        route: KClass<R>,
        viewModel: KClass<VM>,
        content: @Composable (state: S, actions: (A) -> Unit) -> Unit,
    ) where VM : ViewModel, VM : HasActionableState<S, A>, VM : HasInitializer<R>
}