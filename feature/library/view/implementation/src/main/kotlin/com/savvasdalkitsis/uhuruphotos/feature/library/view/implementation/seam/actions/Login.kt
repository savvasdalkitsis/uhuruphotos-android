package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.feature.server.view.api.navigation.ServerNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data object Login : LibraryAction() {
    context(LibraryActionsContext)
    override fun handle(state: LibraryState): Flow<Mutation<LibraryState>> = flow {
        navigator.navigateTo(ServerNavigationRoute)
    }
}