package com.savvasdalkitsis.librephotos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.pedroloureiro.mvflow.HandlerWithEffects
import net.pedroloureiro.mvflow.MVFlow
import net.pedroloureiro.mvflow.Reducer
import timber.log.Timber

open class MVFlowViewModel<STATE, ACTION, MUTATION, EFFECT>(
    handler: HandlerWithEffects<STATE, ACTION, MUTATION, EFFECT>,
    reducer: Reducer<STATE, MUTATION>,
    initialState: STATE,
    initialAction: ACTION? = null,
) : ViewModel() {

    private val _state: MutableLiveData<STATE> = MutableLiveData()
    val state: LiveData<STATE> = _state
    private val _effects: Channel<EFFECT> = Channel()
    val effects: Flow<EFFECT> = _effects.receiveAsFlow()
    val actions: Channel<ACTION> = Channel()

    init {
        val mvFlow = MVFlow(
            initialState = initialState,
            handler = handler,
            reducer = reducer,
            mvflowCoroutineScope = viewModelScope,
            defaultLogger = { Timber.d(it) }
        )
        mvFlow.takeView(viewModelScope, MVFlowView(),
            initialActions = listOfNotNull(initialAction)
        )
        viewModelScope.launch {
            mvFlow.observeEffects().collect { effect ->
                _effects.trySend(effect)
            }
        }
    }

    inner class MVFlowView : MVFlow.View<STATE, ACTION> {

        override fun actions(): Flow<ACTION> = actions.receiveAsFlow()

        override fun render(state: STATE) {
            _state.value = state
        }
    }
}