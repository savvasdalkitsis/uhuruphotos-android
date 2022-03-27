package com.savvasdalkitsis.librephotos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.pedroloureiro.mvflow.HandlerWithEffects
import net.pedroloureiro.mvflow.MVFlow
import net.pedroloureiro.mvflow.Reducer
import timber.log.Timber
import kotlin.coroutines.coroutineContext

open class MVFlowViewModel<STATE, ACTION, MUTATION, EFFECT>(
    handler: HandlerWithEffects<STATE, ACTION, MUTATION, EFFECT>,
    reducer: Reducer<STATE, MUTATION>,
    initialState: STATE,
    private val initialAction: ACTION,
) : ViewModel() {

    private val _state: MutableLiveData<STATE> = MutableLiveData()
    val state: LiveData<STATE> = _state
    private val effects: Channel<EFFECT> = Channel()
    val actions: Channel<ACTION> = Channel()

    init {
        val mvFlow = MVFlow(
            initialState = initialState,
            handler = handler,
            reducer = reducer,
            mvflowCoroutineScope = viewModelScope,
            defaultLogger = { Timber.d(it) }
        )
        mvFlow.takeView(viewModelScope, MVFlowView())
        viewModelScope.launch {
            mvFlow.observeEffects().collect { effect ->
                effects.trySend(effect)
            }
        }
    }

    suspend fun start(
       effectsCollector: FlowCollector<EFFECT>,
    ) {
        CoroutineScope(coroutineContext).launch {
            actions.send(initialAction)
        }
        effects.receiveAsFlow().collect(effectsCollector)
    }

    inner class MVFlowView : MVFlow.View<STATE, ACTION> {

        override fun actions(): Flow<ACTION> = actions.receiveAsFlow()

        override fun render(state: STATE) {
            _state.value = state
        }
    }
}