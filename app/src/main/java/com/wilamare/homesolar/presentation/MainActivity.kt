package com.wilamare.homesolar.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.dataStore
import com.ramcosta.composedestinations.DestinationsNavHost
import com.wilamare.homesolar.domain.preferences.UserPreferences
import com.wilamare.homesolar.domain.preferences.UserPreferencesSerializer
import com.wilamare.homesolar.ui.theme.HomeSolarTheme
import dagger.hilt.android.AndroidEntryPoint

val Context.userDatastore by dataStore("user-preferences.json", UserPreferencesSerializer)
suspend fun Context.setUserPreferences(
    serverUrl: String? = null,
    serverPort: String? = null,
    username: String? = null,
    password: String? = null,
    isLoggedIn: Boolean? = null,
    isRemember: Boolean? = null
) {
    userDatastore.updateData { userPreferences ->
        var preferences = userPreferences
        serverUrl?.let {
            preferences = preferences.copy(serverUrl = it)
        }
        serverPort?.let {
            preferences = preferences.copy(serverPort = it)
        }
        username?.let {
            preferences = preferences.copy(username = it)
        }
        password?.let {
            preferences = preferences.copy(password = it)
        }
        isLoggedIn?.let {
            preferences = preferences.copy(isLoggedIn = it)
        }
        isRemember?.let {
            preferences = preferences.copy(isRemember = it)
        }
        preferences
    }
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeSolarTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}