package com.wilamare.homesolar.presentation.setting

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.wilamare.homesolar.presentation.login.LoginState
import com.wilamare.homesolar.presentation.login.LoginViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(

) : ViewModel() {
    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _state = mutableStateOf(SettingState())
    val state: State<SettingState> = _state
}