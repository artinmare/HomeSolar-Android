package com.wilamare.homesolar.presentation.battery

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class BatteryViewModel @Inject constructor(

): ViewModel() {
    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _state = mutableStateOf(BatteryState())
    val state: State<BatteryState> = _state
}