package com.wilamare.homesolar.presentation.login

data class LoginState(
    val serverUrl: String = "",
    val serverPort: String = "",
    val username: String = "",
    val password: String = "",
    val isRememberMe: Boolean = false
)

data class LoginFieldErrorState(
    val serverUrlErrorMessage: String = "",
    val serverPortErrorMessage: String = "",
    val usernameErrorMessage: String = "",
    val passwordErrorMessage: String = "",

    val isServerUrlError: Boolean = false,
    val isServerPortError: Boolean = false,
    val isUsernameError: Boolean = false,
    val isPasswordError: Boolean = false
)