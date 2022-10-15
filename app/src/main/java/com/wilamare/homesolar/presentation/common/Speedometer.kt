package com.wilamare.homesolar.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun Speedometer(
    modifier: Modifier = Modifier,
    value: Double,
    minValue: Double,
    maxValue: Double,
    startAngle: Float = 90f,
    circleWidth: Dp = 12.dp,
    strokeCap: StrokeCap = StrokeCap.Round,
    color: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = Color.DarkGray,
    isGradient: Boolean = true,
    isPercentage: Boolean = false,
    styledValue: Boolean = true,
    title: String? = null,
    prefix: String = "",
    suffix: String = "",
    textStyle: TextStyle = MaterialTheme.typography.body1,
    fontSize: TextUnit = TextUnit.Unspecified,
) {
    var currentPercentage by remember { mutableStateOf(0f) }
    val percentage = remember {
        Animatable(initialValue = 0f)
    }

    LaunchedEffect(value) {
        currentPercentage = (value / (maxValue - minValue)).toFloat()
        percentage.animateTo(
            targetValue = currentPercentage,
            animationSpec = tween(durationMillis = 1000)
        )
    }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            val textValue = if (!isPercentage) "$prefix${
                String.format(
                    "%.2f",
                    value
                )
            }$suffix" else "${(currentPercentage * 100).toInt()}%"

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = backgroundColor,
                    radius = (size.minDimension / 2.0f) - (circleWidth.toPx() / 2.0f),
                    style = Stroke((circleWidth - 4.dp).toPx(), cap = strokeCap)
                )

                if (isGradient) {
                    drawArc(
                        brush = Brush.linearGradient(
                            colors = mutableListOf(
                                color,
                                color.adjustBrightness(0.8f),
                                color.adjustBrightness(0.5f)
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
                        color = color,
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
            if(styledValue){
                Text(text = textValue, style = textStyle, fontSize = fontSize, color = color.adjustBrightness(.9f), fontWeight = FontWeight.Bold)
            } else {
                Text(text = textValue, style = textStyle, fontSize = fontSize)
            }
        }
        if (title != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.body1)
        }
    }
}


