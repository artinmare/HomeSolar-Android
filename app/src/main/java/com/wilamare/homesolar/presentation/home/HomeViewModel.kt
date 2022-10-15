package com.wilamare.homesolar.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilamare.homesolar.common.Resource
import com.wilamare.homesolar.data.remote.MqttService
import com.wilamare.homesolar.presentation.login.LoginState
import com.wilamare.homesolar.presentation.login.LoginViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mqttService: MqttService
): ViewModel() {
    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private fun getStatus(code: Int):String {
        return when(code){
            0 -> "Standby"
            1 -> "Charging"
            2 -> "Discharging"
            else -> "Unknown"
        }
    }

    fun observerSummary(){
        viewModelScope.launch {
            if (mqttService.isConnected()){
                mqttService.observeSummary()
                    .onEach { summary ->
                        _state.value = state.value.copy(
                            todayGeneration = summary.todayGeneration,
                            todayIndependence = summary.todayIndependence,
                            solar = summary.solar,
                            battery = summary.battery,
                            inverter = summary.inverter,
                            grid = summary.grid,
                            home = summary.home,
                            charge = summary.charge,
                            status = getStatus(summary.status)
                        )
                    }.launchIn(viewModelScope)
            } else {
                mqttService.connect()
                mqttService.observeSummary()
                    .onEach { summary ->
                        _state.value = state.value.copy(
                            todayGeneration = summary.todayGeneration,
                            todayIndependence = summary.todayIndependence,
                            solar = summary.solar,
                            battery = summary.battery,
                            inverter = summary.inverter,
                            grid = summary.grid,
                            home = summary.home
                        )
                    }.launchIn(viewModelScope)
            }
        }
    }
}