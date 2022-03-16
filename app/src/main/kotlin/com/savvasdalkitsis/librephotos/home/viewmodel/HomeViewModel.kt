package com.savvasdalkitsis.librephotos.home.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savvasdalkitsis.librephotos.auth.model.AuthStatus
import com.savvasdalkitsis.librephotos.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.librephotos.feed.state.FeedState
import com.savvasdalkitsis.librephotos.feed.view.preview.feedStatePreview
import com.savvasdalkitsis.librephotos.home.state.HomeViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
) : ViewModel() {

    private val stateFlow =
        MutableSharedFlow<HomeViewState>(extraBufferCapacity = 1)

    init {
        viewModelScope.launch {
            stateFlow.emit(HomeViewState(isLoading = true))
            authenticationUseCase.authenticationStatus().collectLatest {
                when (it) {
                    is AuthStatus.Unauthenticated -> {}
                    else -> stateFlow.emit(HomeViewState(isLoading = false, feedState = feedStatePreview))
                }
            }
        }
    }

    @Composable
    fun state() =
        stateFlow.asSharedFlow().collectAsState(initial = HomeViewState(feedState = FeedState()))

}
