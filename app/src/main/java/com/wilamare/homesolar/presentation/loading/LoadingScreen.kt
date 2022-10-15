package com.wilamare.homesolar.presentation.loading

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.wilamare.homesolar.R
import com.wilamare.homesolar.domain.preferences.UserPreferences
import com.wilamare.homesolar.presentation.destinations.HomeScreenDestination
import com.wilamare.homesolar.presentation.destinations.LoginScreenDestination
import com.wilamare.homesolar.presentation.login.LoginViewModel
import com.wilamare.homesolar.presentation.setUserPreferences
import com.wilamare.homesolar.presentation.userDatastore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@RootNavGraph(start = true)
@Destination
@Composable
fun LoadingScreen(
    navigator: DestinationsNavigator,
    viewModel: LoadingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userPreferences =
        context.userDatastore.data.collectAsState(initial = UserPreferences()).value

    LaunchedEffect(userPreferences){
        delay(500)
        if(userPreferences.isLoggedIn){
            viewModel.connect(context,userPreferences.serverUrl,userPreferences.serverPort,userPreferences.username,userPreferences.password)
        } else {
            navigator.popBackStack()
            navigator.navigate(LoginScreenDestination)
        }
    }

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is LoadingViewModel.UIEvent.Connected -> {
                    navigator.popBackStack()
                    navigator.navigate(HomeScreenDestination)
                }
                is LoadingViewModel.UIEvent.FailedToConnect -> {
                    navigator.popBackStack()
                    navigator.navigate(LoginScreenDestination)
                }
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.size(200.dp),
                painter = painterResource(id = R.drawable.dr_logo),
                contentDescription = "Logo"
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = viewModel.loadingText.value)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(
                        RoundedCornerShape(50)
                    )
            )
        }
    }
}