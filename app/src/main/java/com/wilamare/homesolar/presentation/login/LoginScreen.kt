package com.wilamare.homesolar.presentation.login

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.wilamare.homesolar.domain.preferences.UserPreferences
import com.wilamare.homesolar.presentation.common.OutlinedTextFieldWithError
import com.wilamare.homesolar.presentation.destinations.HomeScreenDestination
import com.wilamare.homesolar.presentation.setUserPreferences
import com.wilamare.homesolar.presentation.userDatastore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Destination
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val userPreferences =
        context.userDatastore.data.collectAsState(initial = UserPreferences()).value
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(userPreferences) {
        if (userPreferences.isRemember) {
            Log.w("UserPreferences", "Remembered")
            viewModel.updateState(userPreferences)
        }
    }

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is LoginViewModel.UIEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(message = event.message)
                }
                is LoginViewModel.UIEvent.Connected -> {
                    context.setUserPreferences(
                        isLoggedIn = true,
                        isRemember = viewModel.state.value.isRememberMe,
                        serverUrl = viewModel.state.value.serverUrl,
                        serverPort = viewModel.state.value.serverPort,
                        username = viewModel.state.value.username,
                        password = viewModel.state.value.password
                    )
                    navigator.popBackStack()
                    navigator.navigate(HomeScreenDestination)
                }
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), scaffoldState = scaffoldState) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Welcome Back", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "Please login to the Server!", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextFieldWithError(
                    modifier = Modifier.fillMaxWidth(.6f),
                    value = viewModel.state.value.serverUrl,
                    onValueChange = { viewModel.onServerUrlChange(it) },
                    isError = viewModel.errorState.value.isServerUrlError,
                    errorMessage = viewModel.errorState.value.serverUrlErrorMessage,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Right)
                        }
                    ),
                    trailingIcon = {
                        if (viewModel.state.value.serverUrl.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onServerUrlChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Server URL"
                                )
                            }
                        }
                    },
                    maxLines = 1,
                    singleLine = true,
                    label = { Text(text = "Server URL", overflow = TextOverflow.Ellipsis) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextFieldWithError(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.state.value.serverPort,
                    onValueChange = { viewModel.onServerPortChange(it) },
                    isError = viewModel.errorState.value.isServerPortError,
                    errorMessage = viewModel.errorState.value.serverPortErrorMessage,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    maxLines = 1,
                    singleLine = true,
                    label = { Text(text = "Server Port", overflow = TextOverflow.Ellipsis) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextFieldWithError(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.state.value.username,
                onValueChange = { viewModel.onUsernameChange(it) },
                isError = viewModel.errorState.value.isUsernameError,
                errorMessage = viewModel.errorState.value.usernameErrorMessage,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                trailingIcon = {
                    if (viewModel.state.value.username.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onUsernameChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Username"
                            )
                        }
                    }
                },
                maxLines = 1,
                singleLine = true,
                label = { Text(text = "Username", overflow = TextOverflow.Ellipsis) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            var isNotVisible by remember { mutableStateOf(true) }
            OutlinedTextFieldWithError(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.state.value.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                isError = viewModel.errorState.value.isPasswordError,
                errorMessage = viewModel.errorState.value.passwordErrorMessage,
                visualTransformation = if (isNotVisible) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = { isNotVisible = isNotVisible.not() }) {
                        Icon(
                            imageVector = if (isNotVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Password Visibility Icon"
                        )
                    }
                },
                maxLines = 1,
                singleLine = true,
                label = { Text(text = "Password", overflow = TextOverflow.Ellipsis) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = viewModel.state.value.isRememberMe,
                    onCheckedChange = { viewModel.toggleRemember() })
                Text(
                    modifier = Modifier.clickable { viewModel.toggleRemember() },
                    text = "Remember Me ?"
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(modifier = Modifier.fillMaxWidth(.8f), onClick = {
                viewModel.login(context)
                focusManager.clearFocus()
            }) {
                Text(text = "Login", style = MaterialTheme.typography.button)
            }
        }
    }
}