package com.wilamare.homesolar.presentation.loading

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilamare.homesolar.common.Resource
import com.wilamare.homesolar.data.remote.MqttService
import com.wilamare.homesolar.presentation.login.LoginViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val mqttService: MqttService
) : ViewModel() {
    sealed class UIEvent {
        object Connected: UIEvent()
        object FailedToConnect: UIEvent()
    }

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _loadingText = mutableStateOf("Please wait while the app is being loaded...")
    val loadingText: State<String> = _loadingText

    fun connect(context: Context, serverUrl:String, serverPort:String, username:String, password:String){
        viewModelScope.launch {
            _loadingText.value = "Connecting to the Server!"
            if (mqttService.isConnected()){
                _eventFlow.emit(UIEvent.Connected)
            } else {
                when(val result = mqttService.initSession(context,username,password,serverUrl,serverPort)){
                    is Resource.Success -> {
                        _loadingText.value = "Connected to the Server!"
                        _eventFlow.emit(UIEvent.Connected)
                    }
                    is Resource.Error -> {
                        _loadingText.value = "Failed to connect to the server, ${result.message ?: "Unknown Error"}"
                        _eventFlow.emit(UIEvent.FailedToConnect)
                    }
                    else -> {
                        _loadingText.value = "Failed to connect to the server, ${result.message ?: "Unknown Error"}"
                        _eventFlow.emit(UIEvent.FailedToConnect)
                    }
                }
            }
        }
    }
}