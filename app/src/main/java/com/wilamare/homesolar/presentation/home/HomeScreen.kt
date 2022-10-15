package com.wilamare.homesolar.presentation.home

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.wilamare.homesolar.R
import com.wilamare.homesolar.common.formattedString
import com.wilamare.homesolar.presentation.common.NavigationItem
import com.wilamare.homesolar.presentation.destinations.*
import com.wilamare.homesolar.presentation.setUserPreferences
import kotlinx.coroutines.launch

@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when(event){
                Lifecycle.Event.ON_START -> {
                    viewModel.observerSummary()
                    Log.w("Lifecycle", "ON_START")
                }
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.observerSummary()
                    Log.w("Lifecycle", "ON_RESUME")
                }
                else -> {
                    Log.w("Lifecycle", "ON_STOP")
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState
    ) {
        BackHandler(enabled = scaffoldState.drawerState.isOpen || scaffoldState.snackbarHostState.currentSnackbarData == null) {
            scope.launch {
                if (scaffoldState.drawerState.isOpen) {
                    scaffoldState.drawerState.close()
                } else {
                    scaffoldState.snackbarHostState.showSnackbar("Tap back once again to close the app !")
                }
            }
        }
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Text(text = "Home Solar", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = viewModel.state.value.status, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Column {
                        Text(text = "Solar", fontSize = 16.sp, fontWeight = FontWeight.Light)
                        Text(
                            text = viewModel.state.value.solar.formattedString(suffix = "W", useSpacing = true),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.width(80.dp))
                    Column {
                        Text(text = "Battery", fontSize = 16.sp, fontWeight = FontWeight.Light)
                        Text(
                            text = "${viewModel.state.value.battery.formattedString(suffix = "W", useSpacing = true)} . ${viewModel.state.value.charge.formattedString(suffix = "%")}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        painter = painterResource(id = R.drawable.dr_house),
                        contentDescription = "Flow illustration"
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Inverter", fontSize = 16.sp, fontWeight = FontWeight.Light)
                        Text(
                            text = viewModel.state.value.inverter.formattedString(suffix = "W", useSpacing = true),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column {
                        Text(text = "Grid", fontSize = 16.sp, fontWeight = FontWeight.Light)
                        Text(
                            text = viewModel.state.value.grid.formattedString(suffix = "W", useSpacing = true),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column {
                        Text(text = "Home", fontSize = 16.sp, fontWeight = FontWeight.Light)
                        Text(
                            text = viewModel.state.value.home.formattedString(suffix = "W", useSpacing = true),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Column {
                NavigationItem(
                    onClick = { navigator.navigate(StatisticScreenDestination) },
                    title = "Statistics",
                    desc = viewModel.state.value.todayGeneration.formattedString(suffix = "Wh Generated Today", useSpacing = true)
                )
                NavigationItem(
                    onClick = { navigator.navigate(BatteryScreenDestination) },
                    title = "Battery",
                    desc = viewModel.state.value.todayIndependence.formattedString(suffix = "% Self-Powered Today")
                )
                NavigationItem(
                    onClick = { navigator.navigate(SettingScreenDestination) },
                    title = "Settings"
                )
                NavigationItem(
                    onClick = { navigator.navigate(AboutScreenDestination) },
                    title = "About"
                )
                NavigationItem(onClick = {
                    scope.launch {
                        context.setUserPreferences(isLoggedIn = false)
                    }
                    navigator.popBackStack()
                    navigator.navigate(LoginScreenDestination)
                }, title = "Logout")
            }
        }
    }
}