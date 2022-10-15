package com.wilamare.homesolar.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.wilamare.homesolar.common.adjustBrightness
import com.wilamare.homesolar.domain.common.PieChartData
import kotlin.math.roundToInt

sealed class LegendsArrangement {
    object Start : LegendsArrangement()
    object End : LegendsArrangement()
    object Top : LegendsArrangement()
    object Bottom : LegendsArrangement()
}

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    chartData: List<PieChartData>,
    startAngle: Float = 90f,
    circleWidth: Dp = 12.dp,
    radius: Dp = 80.dp,
    strokeCap: StrokeCap = StrokeCap.Round,
    backgroundColor: Color = Color.DarkGray,
    isGradient: Boolean = true,
    text: String? = null,
    textStyle: TextStyle = MaterialTheme.typography.body1,
    fontSize: TextUnit = TextUnit.Unspecified,
    enableLegends: Boolean = true,
    legendsArrangement: LegendsArrangement = LegendsArrangement.Start,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    verticalArrangement: Arrangement.Vertical = Arrangement.SpaceBetween,
) {
    var totalValue by remember { mutableStateOf(chartData.sumOf { it.value }) }
    var lastValue by remember { mutableStateOf(totalValue) }
    LaunchedEffect(key1 = chartData) {
        totalValue = chartData.sumOf { it.value }
        lastValue = totalValue
    }
    Column(modifier = modifier, horizontalAlignment = horizontalAlignment, verticalArrangement = verticalArrangement) {
        if (enableLegends && legendsArrangement == LegendsArrangement.Top) {
            Column(Modifier.padding(bottom = 16.dp)) {
                chartData.forEach {
                    LegendRow(data = it, maxValue = totalValue)
                }
            }
        }
        Row(modifier = modifier, horizontalArrangement = horizontalArrangement, verticalAlignment = verticalAlignment) {
            if (enableLegends && legendsArrangement == LegendsArrangement.Start) {
                Column(Modifier.padding(end = 16.dp)) {
                    chartData.forEach {
                        LegendRow(data = it, maxValue = totalValue)
                    }
                }
            }
            Box(
                modifier = Modifier.size(radius),
                contentAlignment = Alignment.Center
            ) {

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = backgroundColor,
                        radius = (size.minDimension / 2.0f) - (circleWidth.toPx() / 2.0f),
                        style = Stroke((circleWidth - 4.dp).toPx(), cap = strokeCap)
                    )
                }

                chartData.asReversed().forEach { pieChartData ->
                    PiePiece(
                        modifier = Modifier.fillMaxSize(),
                        data = pieChartData.copy(value = lastValue),
                        maxValue = totalValue,
                        isGradient = isGradient,
                        circleWidth = circleWidth,
                        startAngle = startAngle,
                        strokeCap = strokeCap
                    )
                    lastValue -= pieChartData.value
                }
                text?.let {
                    Text(text = it, style = textStyle, fontSize = fontSize)
                }
            }
            if (enableLegends && legendsArrangement == LegendsArrangement.End) {
                Column(Modifier.padding(start = 16.dp)) {
                    chartData.forEach {
                        LegendRow(data = it, maxValue = totalValue)
                    }
                }
            }
        }
        if (enableLegends && legendsArrangement == LegendsArrangement.Bottom) {
            Column(Modifier.padding(start = 16.dp)) {
                chartData.forEach {
                    LegendRow(data = it, maxValue = totalValue)
                }
            }
        }
    }
}

@Composable
fun PiePiece(
    modifier: Modifier = Modifier,
    data: PieChartData,
    maxValue: Double,
    isGradient: Boolean,
    circleWidth: Dp,
    startAngle: Float,
    strokeCap: StrokeCap
) {
    var currentPercentage by remember { mutableStateOf(0f) }
    val percentage = remember {
        Animatable(initialValue = 0f)
    }

    LaunchedEffect(maxValue) {
        currentPercentage = (data.value / maxValue).toFloat()
        percentage.animateTo(
            targetValue = currentPercentage,
            animationSpec = tween(durationMillis = 1000)
        )
    }
    Canvas(modifier = modifier) {
        if (isGradient) {
            drawArc(
                color = Color.DarkGray.adjustBrightness(0.8f).copy(alpha = 0.2f),
                topLeft = Offset(
                    (size.width - size.minDimension) / 2.0f + (circleWidth.toPx() / 2.0f),
                    (size.height - size.minDimension) / 2.0f + (circleWidth.toPx() / 2.0f)
                ),
                size = Size(
                    size.minDimension - (circleWidth.toPx()),
                    size.minDimension - (circleWidth.toPx())
                ),
                startAngle = startAngle - 5.4f,
                sweepAngle = ((percentage.value + 0.03f) * 360),
                useCenter = false,
                style = Stroke(circleWidth.toPx(), cap = strokeCap)
            )
            drawArc(
                brush = Brush.linearGradient(
                    colors = mutableListOf(
                        data.color,
                        data.color.adjustBrightness(0.8f),
                        data.color.adjustBrightness(0.5f)
                    )
                ),
                topLeft = Offset(
                    (size.width - size.minDimension) / 2.0f + (circleWidth.toPx() / 2.0f),
                    (size.height - size.minDimension) / 2.0f + (circleWidth.toPx() / 2.0f)
                ),
                size = Size(
                    size.minDimension - (circleWidth.toPx()),
                    size.minDimension - (circleWidth.toPx())
                ),
                startAngle = startAngle,
                sweepAngle = (percentage.value * 360),
                useCenter = false,
                style = Stroke(circleWidth.toPx(), cap = strokeCap)
            )
        } else {
            drawArc(
                color = Color.DarkGray.adjustBrightness(0.8f).copy(alpha = 0.2f),
                topLeft = Offset(
                    (size.width - size.minDimension) / 2.0f + (circleWidth.toPx() / 2.0f),
                    (size.height - size.minDimension) / 2.0f + (circleWidth.toPx() / 2.0f)
                ),
                size = Size(
                    size.minDimension - (circleWidth.toPx()),
                    size.minDimension - (circleWidth.toPx())
                ),
                startAngle = startAngle - 5.4f,
                sweepAngle = ((percentage.value + 0.03f) * 360),
                useCenter = false,
                style = Stroke(circleWidth.toPx(), cap = strokeCap)
            )
            drawArc(
                color = data.color,
                topLeft = Offset(
                    (size.width - size.minDimension) / 2.0f + (circleWidth.toPx() / 2.0f),
                    (size.height - size.minDimension) / 2.0f + (circleWidth.toPx() / 2.0f)
                ),
                size = Size(
                    size.minDimension - (circleWidth.toPx()),
                    size.minDimension - (circleWidth.toPx())
                ),
                startAngle = startAngle,
                sweepAngle = (percentage.value * 360),
                useCenter = false,
                style = Stroke(circleWidth.toPx(), cap = strokeCap)
            )
        }
    }
}

@Composable
fun LegendRow(
    data: PieChartData,
    maxValue: Double
) {
    var currentPercentage by remember { mutableStateOf(0f) }
    val percentage = remember {
        Animatable(initialValue = 0f)
    }

    LaunchedEffect(maxValue) {
        currentPercentage = (data.value / maxValue).toFloat()
        percentage.animateTo(
            targetValue = currentPercentage,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier.width(40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(data.color)
            )
            Text(
                text = "${(percentage.value * 100).roundToInt()}%",
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            modifier = Modifier.padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = data.title,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = " | ${data.value.roundToInt()}",
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}