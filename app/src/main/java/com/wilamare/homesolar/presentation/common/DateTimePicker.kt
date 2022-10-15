package com.wilamare.homesolar.presentation.common

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
@Composable
fun MonthYearPicker(title: String? = "Select month to be displayed", date: Date = Date(), onConfirm: (String, String) -> Unit, onCancel: () -> Unit) {

    val currentDate = Date()
    val currentYear = SimpleDateFormat("yyyy").format(currentDate).toString()

    var selectedMonth by remember { mutableStateOf(SimpleDateFormat("MMM").format(date).toString().uppercase()) }
    var selectedYear by remember { mutableStateOf(SimpleDateFormat("yyyy").format(date).toString()) }
    var selecting by remember { mutableStateOf("Month") }

    val years = (1970..currentYear.toInt()).toList().asReversed()
    val months = listOf("JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC")
    Column(modifier = Modifier
        .fillMaxHeight(.7f)
        .fillMaxWidth()
        .clip(RoundedCornerShape(4.dp))
        .background(MaterialTheme.colors.surface)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary)
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            title?.let {
                Text(text = it, fontSize = 16.sp, color = MaterialTheme.colors.onPrimary, textAlign = TextAlign.Center)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = { selecting = "Month" }) {
                    Text(text = selectedMonth, color = if (selecting == "Month") MaterialTheme.colors.onPrimary else MaterialTheme.colors.onPrimary.copy(alpha = .5f), fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(32.dp))
                TextButton(onClick = { selecting = "Year" }) {
                    Text(text = selectedYear, color = if (selecting == "Year") MaterialTheme.colors.onPrimary else MaterialTheme.colors.onPrimary.copy(alpha = .5f), fontSize = 20.sp)
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth().weight(1f).padding(16.dp)) {
            if (selecting == "Month") {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize(),
                    columns = GridCells.Fixed(4)
                ) {
                    items(months){ month ->
                        Box(modifier = Modifier
                            .size(75.dp)
                            .clip(CircleShape)
                            .background(if (selectedMonth == month) MaterialTheme.colors.primary else MaterialTheme.colors.surface)
                            .clickable { selectedMonth = month }, contentAlignment = Alignment.Center) {
                            Text(text = month, color = if(selectedMonth == month) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(years){ year ->
                        TextButton(modifier = Modifier.fillMaxWidth(),onClick = { selectedYear = year.toString() }) {
                            Text(text = year.toString(), color = if(selectedYear == year.toString()) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface)
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onCancel) {
                Text(text = "Cancel")
            }
            Spacer(modifier = Modifier.width(16.dp))
            TextButton(onClick = { onConfirm(selectedMonth,selectedYear) }) {
                Text(text = "Ok")
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun YearPicker(title: String? = "Select Year to be displayed", date: Date = Date(), onConfirm: (String) -> Unit, onCancel: () -> Unit) {
    val currentDate = Date()
    val currentYear = SimpleDateFormat("yyyy").format(currentDate).toString()

    var selectedYear by remember { mutableStateOf(SimpleDateFormat("yyyy").format(date).toString()) }
    val years = (1970..currentYear.toInt()).toList().asReversed()
    Column(modifier = Modifier
        .fillMaxHeight(.7f)
        .fillMaxWidth()
        .clip(RoundedCornerShape(4.dp))
        .background(MaterialTheme.colors.surface)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary)
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            title?.let {
                Text(text = it, fontSize = 16.sp, color = MaterialTheme.colors.onPrimary, textAlign = TextAlign.Center)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = selectedYear, color = MaterialTheme.colors.onPrimary, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Column(modifier = Modifier.fillMaxWidth().weight(1f).padding(16.dp)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(years){ year ->
                        TextButton(modifier = Modifier.fillMaxWidth(),onClick = { selectedYear = year.toString() }) {
                            Text(text = year.toString(), color = if(selectedYear == year.toString()) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface)
                        }
                    }
                }

        }
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onCancel) {
                Text(text = "Cancel")
            }
            Spacer(modifier = Modifier.width(16.dp))
            TextButton(onClick = { onConfirm(selectedYear) }) {
                Text(text = "Ok")
            }
        }
    }
}