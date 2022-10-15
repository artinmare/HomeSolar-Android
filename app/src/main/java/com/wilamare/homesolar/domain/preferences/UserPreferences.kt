package com.wilamare.homesolar.domain.preferences

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val serverUrl: String = "",
    val serverPort: String = "",
    val username: String = "",
    val password: String = "",
    val isLoggedIn: Boolean = false,
    val isRemember: Boolean = false
)

