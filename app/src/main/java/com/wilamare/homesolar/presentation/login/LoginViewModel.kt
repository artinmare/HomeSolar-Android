package com.wilamare.homesolar.presentation.login

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilamare.homesolar.common.Resource
import com.wilamare.homesolar.data.remote.MqttService
import com.wilamare.homesolar.domain.preferences.UserPreferences
import com.wilamare.homesolar.presentation.setUserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val mqttService: MqttService
) : ViewModel() {
    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
        object Connected: UIEvent()
    }

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state

    private val _errorState = mutableStateOf(LoginFieldErrorState())
    val errorState: State<LoginFieldErrorState> = _errorState

    fun updateState(userPreference:UserPreferences){
        _state.value = state.value.copy(
            serverUrl = userPreference.serverUrl,
            serverPort = userPreference.serverPort,
            username = userPreference.username,
            password = userPreference.password,
            isRememberMe = userPreference.isRemember
        )
    }
    fun onServerUrlChange(text: String){
        _state.value = state.value.copy(serverUrl = text.trim {it.isWhitespace()})
        if(errorState.value.isServerUrlError){
            _errorState.value = errorState.value.copy(isServerUrlError = false)
        }
    }

    fun onServerPortChange(text: String){
        _state.value = state.value.copy(serverPort = if(text.length > 1) text.filter { it.isDigit() }.trimStart{ it == '0' }.take(5) else text.filter { it.isDigit() }.trimStart{ it == '0' } )
        if (errorState.value.isServerPortError){
            _errorState.value = errorState.value.copy(isServerPortError = false)
        }
    }

    fun onUsernameChange(text: String){
        _state.value = state.value.copy(username = text.trim {it.isWhitespace()})
        if (errorState.value.isUsernameError){
            _errorState.value = errorState.value.copy(isUsernameError = false)
        }
    }

    fun onPasswordChange(text: String){
        _state.value = state.value.copy(password = text)
        if(errorState.value.isPasswordError){
            _errorState.value = errorState.value.copy(isPasswordError = false)
        }
    }

    fun toggleRemember() {
        _state.value = state.value.copy(isRememberMe = state.value.isRememberMe.not())
    }

    fun login(context: Context){
        if (isInputValid()) {
            viewModelScope.launch {
                _eventFlow.emit(UIEvent.ShowSnackbar("Connecting to the Server!"))
                if (mqttService.isConnected()){
                    _eventFlow.emit(UIEvent.Connected)
                } else {
                    when(val result = mqttService.initSession(context,state.value.username,state.value.password,state.value.serverUrl, state.value.serverPort)){
                        is Resource.Success -> {
                            _eventFlow.emit(UIEvent.ShowSnackbar("Connected to the server!"))
                            _eventFlow.emit(UIEvent.Connected)
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UIEvent.ShowSnackbar("Failed to connect to the server, ${result.message ?: "Unknown Error"}"))
                        }
                        else -> {
                            _eventFlow.emit(UIEvent.ShowSnackbar("Failed to connect to the server, ${result.message ?: "Unknown Error"}"))
                        }
                    }
                }
            }
        }
    }


    private fun isInputValid(): Boolean {
        _errorState.value = errorState.value.copy(
            isServerUrlError = checkIfBlankOrEmpty(state.value.serverUrl) || state.value.serverUrl.matches(webURLPattern.toRegex()).not(),
            isServerPortError = checkIfBlankOrEmpty(state.value.serverPort) || state.value.serverPort.toInt() > 65535,
            isUsernameError = checkIfBlankOrEmpty(state.value.username) || state.value.username.matches(usernamePattern.toRegex()).not(),
            isPasswordError = checkIfBlankOrEmpty(state.value.password),
            serverUrlErrorMessage = if(checkIfBlankOrEmpty(state.value.serverUrl)) "Server URL can't be blank" else "Please input a valid server URL",
            serverPortErrorMessage = if(checkIfBlankOrEmpty(state.value.serverPort)) "Server port can't be blank" else "Please input a valid server port",
            usernameErrorMessage = if(checkIfBlankOrEmpty(state.value.username)) "Username can't be blank" else "Please input a valid username",
            passwordErrorMessage = "Password can't be blank",
        )
        return (errorState.value.isServerUrlError || errorState.value.isServerPortError || errorState.value.isUsernameError || errorState.value.isPasswordError).not()
    }

    private fun checkIfBlankOrEmpty(text: String): Boolean {
        if (text.isBlank() || text.isEmpty()) {
            return true
        }
        return false
    }

    private val usernamePattern: Pattern = Pattern.compile(
        StringBuilder()
            .append("(?!.*[\\.\\-\\_]{2,})^[a-zA-Z0-9\\.\\-\\_]{3,24}").toString()
    )

    private val webURLPattern: Pattern = Pattern.compile(
        StringBuilder()
            .append("((?:(http|https|Http|Https|rtsp|Rtsp):")
            .append("\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)")
            .append("\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_")
            .append("\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?")
            .append("((?:(?:[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}\\.)+") // named host
            .append("(?:") // plus top level domain
            .append("(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])")
            .append("|(?:biz|b[abdefghijmnorstvwyz])")
            .append("|(?:cat|com|coop|c[acdfghiklmnoruvxyz])")
            .append("|d[ejkmoz]")
            .append("|(?:edu|e[cegrstu])")
            .append("|f[ijkmor]")
            .append("|(?:gov|g[abdefghilmnpqrstuwy])")
            .append("|h[kmnrtu]")
            .append("|(?:info|int|i[delmnoqrst])")
            .append("|(?:jobs|j[emop])")
            .append("|k[eghimnrwyz]")
            .append("|l[abcikrstuvy]")
            .append("|(?:mil|mobi|museum|m[acdghklmnopqrstuvwxyz])")
            .append("|(?:name|net|n[acefgilopruz])")
            .append("|(?:org|om)")
            .append("|(?:pro|p[aefghklmnrstwy])")
            .append("|qa")
            .append("|r[eouw]")
            .append("|s[abcdeghijklmnortuvyz]")
            .append("|(?:tel|travel|t[cdfghjklmnoprtvwz])")
            .append("|u[agkmsyz]")
            .append("|v[aceginu]")
            .append("|w[fs]")
            .append("|y[etu]")
            .append("|z[amw]))")
            .append("|(?:(?:25[0-5]|2[0-4]") // or ip address
            .append("[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]")
            .append("|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]")
            .append("[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}")
            .append("|[1-9][0-9]|[0-9])))")
            .append("(?:\\:\\d{1,5})?)") // plus option port number
            .append("(\\/(?:(?:[a-zA-Z0-9\\;\\/\\?\\:\\@\\&\\=\\#\\~") // plus option query params
            .append("\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?")
            .append("(?:\\b|$)").toString()
    )

}