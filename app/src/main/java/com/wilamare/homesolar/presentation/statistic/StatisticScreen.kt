package com.wilamare.homesolar.presentation.statistic

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.wilamare.homesolar.R
import com.wilamare.homesolar.common.formattedString
import com.wilamare.homesolar.presentation.common.*
import com.wilamare.homesolar.ui.theme.*
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun StatisticScreen(
    navigator: DestinationsNavigator,
    viewModel: StatisticViewModel = hiltViewModel()
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val composeScope = rememberCoroutineScope()
    val context = LocalContext.current as AppCompatActivity
    val chartDatasets = viewModel.getChartDatasets()
    var monthPickerDialog by remember { mutableStateOf(false) }
    var yearPickerDialog by remember { mutableStateOf(false) }
    var datePickerDialog by remember { mutableStateOf(false)}
    val datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

    datePicker.addOnPositiveButtonClickListener {
        viewModel.setDateSelected(it)
        datePickerDialog = false
    }
    datePicker.addOnDismissListener {
        datePickerDialog = false
    }
    datePicker.addOnCancelListener {
        datePickerDialog = false
    }

    if (monthPickerDialog) {
        Dialog(onDismissRequest = { monthPickerDialog = false }) {
            MonthYearPicker(date = viewModel.state.value.selectedDate,onConfirm = { month, year ->
                viewModel.setDateSelected(month = month, year = year)
                monthPickerDialog = false
            }, onCancel = { monthPickerDialog = false })
        }
    }
    if (yearPickerDialog) {
        Dialog(onDismissRequest = { yearPickerDialog = false }) {
            YearPicker(date = viewModel.state.value.selectedDate,onConfirm = { year ->
                viewModel.setDateSelected(year = year)
                yearPickerDialog = false
            }, onCancel = { yearPickerDialog = false })
        }
    }
    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.size(24.dp),
                        onClick = { composeScope.launch { scaffoldState.bottomSheetState.collapse() } }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Timescale"
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Timescale", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Timescale.values().forEach {
                Column(modifier = Modifier
                    .clickable {
                        viewModel.setTimescale(it)
                        composeScope.launch { scaffoldState.bottomSheetState.collapse() }
                    }
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 32.dp)
                ) {
                    Text(text = it.name, fontSize = 16.sp, fontWeight = FontWeight.Light)
                }
            }
        },
        topBar = {
            TopAppBar {
                IconButton(onClick = { navigator.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Go back to Dashboard"
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        onClick = {
                            viewModel.viewModelScope.launch {
                                try {
                                    when(viewModel.state.value.timescale){
                                        Timescale.DAY ->
                                            if(!datePickerDialog){
                                                datePickerDialog = true
                                                datePicker.show(context.supportFragmentManager, "DatePicker")
                                            }
                                        Timescale.MONTH ->
                                            monthPickerDialog = true
                                        Timescale.YEAR ->
                                            yearPickerDialog = true
                                    }
                                }
                                catch (e: Exception){
                                    Log.e("DatePickerButton", e.toString())
                                }
                            }
                        },
                    ) {
                        Text(text = viewModel.getSelectedDateString())
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        onClick = { composeScope.launch { if (scaffoldState.bottomSheetState.isExpanded) scaffoldState.bottomSheetState.collapse() else scaffoldState.bottomSheetState.expand() } }) {
                        Text(text = viewModel.getSelectedTimescale())
                    }
                    Spacer(modifier = Modifier.width(8.dp))
//                    val icon = when (viewModel.chartState.value.chartType) {
//                        ChartType.LINE ->
//                            if (viewModel.chartState.value.isAggregated) Icons.Filled.StackedLineChart else Icons.Outlined.ShowChart
//                        ChartType.BAR ->
//                            if (viewModel.chartState.value.isAggregated) Icons.Filled.StackedBarChart else Icons.Outlined.BarChart
//                    }
//                    IconToggleButton(
//                        checked = viewModel.chartState.value.isAggregated,
//                        onCheckedChange = { viewModel.toggleAggregated() }
//                    ) {
//                        Icon(
//                            imageVector = icon,
//                            contentDescription = "Toggle Aggregated Data"
//                        )
//                    }
                }
            }
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = viewModel.getDataDescription(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                        Text(
                            text = chartDatasets.first().points.sumOf { it.value }.formattedString(suffix = "Wh", noDecimal = true, useSpacing = true),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        StatisticData.values().forEach { data ->
                            val icon = when (data) {
                                StatisticData.HOME ->
                                    Icons.Default.Home
                                StatisticData.SOLAR ->
                                    Icons.Default.WbSunny
                                StatisticData.BATTERY ->
                                    Icons.Default.BatteryFull
                                StatisticData.INVERTER ->
                                    ImageVector.vectorResource(id = R.drawable.ic_inverter)
                                StatisticData.GRID ->
                                    Icons.Default.Bolt
                            }
                            val color = when (data) {
                                StatisticData.HOME ->
                                    PrimaryBlue
                                StatisticData.SOLAR ->
                                    PrimaryYellow
                                StatisticData.BATTERY ->
                                    PrimaryGreen
                                StatisticData.INVERTER ->
                                    PrimaryRed
                                StatisticData.GRID ->
                                    PrimaryPurple
                            }
                            val isSelected = data == viewModel.state.value.selectedData
                            val iconBackground = if (isSelected) color else Color.Transparent
                            val tint = if (isSelected) MaterialTheme.colors.onPrimary else color

                            IconToggleButton(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(iconBackground),
                                checked = isSelected,
                                onCheckedChange = { viewModel.setSelectedData(data) }) {
                                Icon(
                                    imageVector = icon,
                                    tint = tint,
                                    contentDescription = "Toggle" + data.name
                                )
                            }
                        }
                    }
                }
                if(viewModel.state.value.timescale != Timescale.DAY){
                    Row(modifier = Modifier.fillMaxWidth(.75f), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = "Selected ${viewModel.state.value.timescale}".lowercase()
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }, fontWeight = FontWeight.Light)
                            Text(text = viewModel.getSelectedBarDate(), fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text(text = "Selected Value", fontWeight = FontWeight.Light)
                            Text(text = viewModel.chartState.value.selectedPoint.value.formattedString(suffix = "Wh", useSpacing = true), fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    if (viewModel.state.value.timescale == Timescale.DAY) {
                        LineChart(
                            data = chartDatasets,
                            selectedData = viewModel.state.value.selectedData,
                            drawArea = true
                        )
                    } else {
                        BarChartCanvas(data = chartDatasets, barSelected = {viewModel.setSelectedPoint(it)}, drawValue = false,formatter = viewModel.getFormatter())
                    }
                }
                if (viewModel.state.value.selectedData == StatisticData.BATTERY && viewModel.state.value.timescale == Timescale.DAY) {
                    Text(text = "Battery Charge Level", fontWeight = FontWeight.Light)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {
                        LineChart(
                            selectedData = StatisticData.BATTERY,
                            data = viewModel.getChartDatasets(),
                            drawArea = true,
                            valueSuffix = "%",
                            maxValueDescription = "",
                            minValueDescription = ""
                        )
                    }
                }
            }
            if (viewModel.state.value.timescale != Timescale.DAY) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            modifier = Modifier.width(100.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "123 kWh",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Low",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Light
                            )
                        }
                        Column(
                            modifier = Modifier.width(100.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "123 kWh",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Avg",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Light
                            )
                        }
                        Column(
                            modifier = Modifier.width(100.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "123 kWh",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "High",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }

                }
            }
        }
    }
}
