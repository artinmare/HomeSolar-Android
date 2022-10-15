package com.wilamare.homesolar.presentation.battery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.wilamare.homesolar.domain.battery.Cell
import com.wilamare.homesolar.presentation.common.BasicTextBox
import com.wilamare.homesolar.presentation.common.CellItem
import com.wilamare.homesolar.presentation.common.Speedometer
import com.wilamare.homesolar.presentation.common.TopBar
import com.wilamare.homesolar.ui.theme.PrimaryPurple
import com.wilamare.homesolar.ui.theme.PrimaryRed
import kotlinx.coroutines.launch

@Destination
@Composable
fun BatteryScreen(
    navigator: DestinationsNavigator,
    viewModel: BatteryViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                title = "Battery",
                icon = Icons.Default.ArrowBack,
                contentDescription = "Go back to Dashboard",
                onNavigationIconClick = {
                    scope.launch {
                        navigator.navigateUp()
                    }
                }
            )
        }) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column {
                    Spacer(modifier = Modifier.height(32.dp))
                    Speedometer(
                        value = viewModel.state.value.voltage,
                        minValue = 0.0,
                        maxValue = 100.0,
                        suffix = " V",
                        title = "Voltage"
                    )
                }
                Speedometer(
                    value = viewModel.state.value.amperage,
                    minValue = 0.0,
                    maxValue = 100.0,
                    suffix = " A",
                    title = "Amperage",
                    color = PrimaryRed
                )
                Column {
                    Spacer(modifier = Modifier.height(32.dp))
                    Speedometer(
                        color = PrimaryPurple,
                        value = viewModel.state.value.power,
                        minValue = 0.0,
                        maxValue = 10000.0,
                        suffix = " W",
                        title = "Power"
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Speedometer(
                    modifier = Modifier.size(120.dp),
                    startAngle = 270f,
                    circleWidth = 14.dp,
                    value = viewModel.state.value.charge,
                    minValue = 0.0,
                    maxValue = 100.0,
                    isGradient = true,
                    isPercentage = true,
                    color = MaterialTheme.colors.secondary,
                    textStyle = MaterialTheme.typography.h6,
                    title = "State of Charge"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                BasicTextBox(
                    modifier = Modifier.weight(1f),
                    title = "Status",
                    subtitle = viewModel.state.value.status,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextBox(
                    modifier = Modifier.weight(1f),
                    title = "Balance",
                    subtitle = viewModel.state.value.balance,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                BasicTextBox(
                    modifier = Modifier.weight(1f),
                    title = "Average Voltage",
                    subtitle = String.format("%.3f",viewModel.state.value.cells.avgValue()) + " V",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextBox(
                    modifier = Modifier.weight(1f),
                    title = "Voltage Diff",
                    subtitle = String.format("%.3f",viewModel.state.value.cells.maxValue() - viewModel.state.value.cells.minValue()) + " V",
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                BasicTextBox(
                    modifier = Modifier.weight(1f),
                    title = "Highest Voltage",
                    subtitle = String.format("%.3f",viewModel.state.value.cells.maxValue()) + " V",
                    description = "[${viewModel.state.value.cells.find { it.value == viewModel.state.value.cells.maxValue()}?.title}]",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextBox(
                    modifier = Modifier.weight(1f),
                    title = "Lowest Voltage",
                    subtitle = String.format("%.3f",viewModel.state.value.cells.minValue()) + " V",
                    description = "[${viewModel.state.value.cells.find { it.value == viewModel.state.value.cells.minValue()}?.title}]",
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                modifier = Modifier.height(400.dp),
                columns = GridCells.Adaptive(minSize = 100.dp)
            ) {
                items(viewModel.state.value.cells.size) { index ->
                    CellItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp), cell = viewModel.state.value.cells[index]
                    )
                }
            }

        }
    }
}

fun List<Cell>.maxValue(): Double = maxByOrNull { it.value }?.value ?: 0.0
fun List<Cell>.minValue(): Double = minByOrNull { it.value }?.value ?: 0.0
fun List<Cell>.avgValue(): Double = try {sumOf{ it.value}/this.size} catch (e: Exception) {0.0}
