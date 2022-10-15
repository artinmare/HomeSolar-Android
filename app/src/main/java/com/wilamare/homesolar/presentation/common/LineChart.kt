package com.wilamare.homesolar.presentation.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.wilamare.homesolar.presentation.statistic.StatisticData
import com.wilamare.homesolar.ui.theme.*
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun LineChart(
    selectedData: StatisticData,
    data: List<ChartDataset>,
    drawArea: Boolean = false,
    isAggregated: Boolean = false,
    valueSuffix: String = " W",
    maxValueDescription: String = data[0].maxValueDescription,
    minValueDescription: String = data[0].minValueDescription,
) {
    val timescaleLocations = remember { mutableStateListOf<Float>()}
    val zeroLocation = remember { mutableStateOf(0f) }

    val minValue = if (data[0].points.minValue() < 0) abs(data[0].points.minValue()) else 0.0
    val maxValue = data[0].points.maxValue()

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            BoxWithConstraints(modifier = Modifier.weight(1f)) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds()
                ) {
                    val height = size.height
                    val width = size.width
                    val zeroLocationPx = calculateYCoordinate(
                        valueRange = minValue + maxValue,
                        currentValue = minValue,
                        canvasHeight = height
                    )
                    zeroLocation.value = 1f - (zeroLocationPx / height)

                    //Draw Y AxisLine

                    timescaleLocations.removeAll { it == it }
                    for (i in 0..4) {
                        val x = i / 4f * width
                        timescaleLocations.add(i / 4f)
                        drawLine(
                            color = Color.Gray,
                            start = Offset(x = x, y = height),
                            end = Offset(x = x, y = 0f)
                        )
                    }
                    //Draw X AxisLine
                    drawLine(
                        color = Color.Gray,
                        start = Offset(x = 0f, y = zeroLocationPx),
                        end = Offset(x = width, y = zeroLocationPx)
                    )
                    var lastNormX = 0f
                    if (isAggregated) {

                    } else {
                        val color = data[0].color
                        val size = data[0].points.size
                        var distance = 0f

                        val gradientPath = Path()
                        gradientPath.moveTo(0f, zeroLocationPx)
                        data[0].points.forEachIndexed { index, point ->
                            val normX = index * width / size
                            val normY = calculateYCoordinate(
                                valueRange = minValue + maxValue,
                                currentValue = point.value + minValue,
                                canvasHeight = height
                            )

                            if (index < size - 1) {
                                val offsetStart = Offset(normX, normY)
                                val nextNormXPoint = (index + 1) * width / size

                                val nextNormYPoint = calculateYCoordinate(
                                    valueRange = minValue + maxValue,
                                    currentValue = data[0].points[index + 1].value + minValue,
                                    canvasHeight = height
                                )
                                val offsetEnd = Offset(nextNormXPoint, nextNormYPoint)

                                distance += nextNormXPoint - normX
                                drawLine(
                                    Color(color.toArgb()).copy(alpha = 0.8f),
                                    offsetStart,
                                    offsetEnd,
                                    strokeWidth = Stroke.DefaultMiter
                                )
                            } else {
                                lastNormX = normX
                            }
                            with(
                                gradientPath
                            ) {
                                lineTo(normX, normY)
                            }
                        }
                        if (drawArea) {
                            with(
                                gradientPath
                            ) {
                                lineTo(lastNormX, zeroLocationPx)
                                lineTo(0f, zeroLocationPx)
                                close()
                                drawPath(
                                    this,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            color.copy(alpha = 0.7f),
                                            color.copy(alpha = 0.7f),
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }
            ConstraintLayout(modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)) {
                timescaleLocations.forEachIndexed { index, value ->
                    val guideline = createGuidelineFromStart(value)
                    val ref = createRef()
                    Text(
                        modifier = Modifier.constrainAs(ref) {
                            top.linkTo(parent.top, margin = 8.dp)
                            start.linkTo(guideline)
                        },
                        text = getTimescaleString(index),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(80.dp)
                .padding(start = 8.dp, bottom = 33.dp)
        ) {
            if (zeroLocation.value != 1f) {
                Column {
                    Text(
                        text = "${maxValue.toInt()}$valueSuffix",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = maxValueDescription, fontSize = 10.sp, fontWeight = FontWeight.Light)
                }
            }
            ConstraintLayout(modifier = Modifier.weight(1f)) {
                val zeroRef = createRef()
                val zeroGuideline = createGuidelineFromBottom(zeroLocation.value)

                Text(modifier = Modifier.constrainAs(zeroRef) {
                    bottom.linkTo(zeroGuideline)
                    start.linkTo(parent.start)
                }, text = "0$valueSuffix", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            if (zeroLocation.value != 0f) {
                Column {
                    Text(
                        text = "-${minValue.toInt()}$valueSuffix",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = minValueDescription, fontSize = 10.sp, fontWeight = FontWeight.Light)
                }
            }
        }
    }
}

data class ChartDataset(
    val name: StatisticData,
    val color: Color = when (name) {
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
    },
    val maxValueDescription: String = when(name){
        StatisticData.HOME ->
            "Power Usage"
        StatisticData.SOLAR ->
            "Power Production"
        StatisticData.BATTERY ->
            "Power Discharged"
        StatisticData.INVERTER ->
            "Power Generated"
        StatisticData.GRID ->
            "Power Imported"
    },
    val minValueDescription: String = when(name){
        StatisticData.BATTERY ->
            "Charged"
        StatisticData.GRID ->
            "Exported"
        else ->
            ""
    },
    val points: List<ChartData> = emptyList(),
)

fun List<ChartData>.maxValue(): Double = maxByOrNull { it.value }?.value ?: 1.0
fun List<ChartData>.minValue(): Double = minByOrNull { it.value }?.value ?: 0.0

data class ChartData(
    val timestamp: Long,
    val value: Double
)

private fun getTimescaleString(index: Int):String{
    return when(index){
        0 -> "12AM"
        1 -> "6AM"
        2 -> "12PM"
        3 -> "6PM"
        else -> ""
    }
}

fun calculateYCoordinate(
    valueRange: Double,
    currentValue: Double,
    canvasHeight: Float
): Float {
    val maxAndCurrentValueDifference = (valueRange - currentValue)
        .toFloat()
    val relativePercentageOfScreen = (canvasHeight / valueRange)
        .toFloat()
    return (maxAndCurrentValueDifference * relativePercentageOfScreen).roundToInt().toFloat()
}
