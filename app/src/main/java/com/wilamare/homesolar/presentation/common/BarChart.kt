package com.wilamare.homesolar.presentation.common

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.wilamare.homesolar.common.adjustBrightness
import com.wilamare.homesolar.common.formattedString
import com.wilamare.homesolar.common.tapOrPress
import com.wilamare.homesolar.presentation.statistic.StatisticData
import com.wilamare.homesolar.presentation.statistic.Timescale
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Formatter
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun BarChart(
    selectedData: StatisticData,
    data: List<ChartDataset>,
    isAggregated: Boolean = false,
    valueSuffix: String = " W",
    maxValueDescription: String = data[0].maxValueDescription,
    minValueDescription: String = data[0].minValueDescription,
) {
    val timescaleLocations = remember { mutableStateListOf<Float>() }
    val zeroLocation = remember { mutableStateOf(0f) }

    val minValue = if (data[0].points.minValue() < 0) abs(data[0].points.minValue()) else 0.0
    val maxValue = data[0].points.maxValue()

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            BoxWithConstraints(modifier = Modifier.weight(1f)) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val height = size.height
                    val width = size.width
                    val zeroLocationPx = calculateYCoordinate(
                        valueRange = minValue + maxValue,
                        currentValue = minValue,
                        canvasHeight = height
                    )
                    zeroLocation.value = 1f - (zeroLocationPx / height)

                    if (isAggregated) {

                    } else {
                        val color = data[0].color
                        val size = data[0].points.size

                        timescaleLocations.removeAll { it == it }
                        data[0].points.forEachIndexed { index, point ->
                            val normX = (index * width / size) + 2.dp.toPx()
                            val normY = calculateYCoordinate(
                                valueRange = minValue + maxValue,
                                currentValue = point.value + minValue,
                                canvasHeight = height
                            )

                            timescaleLocations.add(index / size.toFloat())
                            val offsetStart = Offset(normX, zeroLocationPx)
                            val offsetEnd = Offset(normX, normY)

                            drawLine(
                                Color(color.toArgb()).copy(alpha = 0.8f),
                                offsetStart,
                                offsetEnd,
                                strokeWidth = 6.dp.toPx(),
                                cap = StrokeCap.Round
                            )
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
                        text = "${index+1}",
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

@SuppressLint("SimpleDateFormat")
@Composable
fun BarChartCanvas(drawValue:Boolean = false, valueSuffix: String = "W", formatter:SimpleDateFormat? = null, data: List<ChartDataset>, barSelected: (ChartData) -> Unit) {
    var offset by remember { mutableStateOf(0f) }
    val dataset = data.first()
    val density = LocalDensity.current
    val horizontalPadding = with(density) { 12.dp.toPx() }
    val distance = with(density) { 26.dp.toPx() }
    val calculatedWidth =
        with(density) { (distance.times(dataset.points.size - 1) + horizontalPadding.times(2)).toDp() }
    val barWidth = with(density) { 12.dp.toPx() }
    val selectionWidth = with(density) { 20.dp.toPx() }
    val smallPadding = with(density) { 4.dp.toPx() }
    val textSize = with(density) { 10.sp.toPx() }
    val legendTextSize = with(density) { 10.sp.toPx() }
    val cornerRadius = with(density) { 4.dp.toPx() }
    val labelSectionHeight = smallPadding.times(2) + textSize

    val pointPaint = Paint().apply {
        color = MaterialTheme.colors.onBackground.toArgb()
        textAlign = Paint.Align.CENTER
        this.textSize = textSize
        isAntiAlias = true
    }
    val xAxisPaint = Paint().apply {
        color = MaterialTheme.colors.onBackground.toArgb()
        textAlign = Paint.Align.CENTER
        this.textSize = textSize
        isAntiAlias = true
    }
    val valuePaint = Paint().apply {
        color = MaterialTheme.colors.onBackground.toArgb()
        textAlign = Paint.Align.LEFT
        this.textSize = legendTextSize
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }
    val descPaint = Paint().apply {
        color = MaterialTheme.colors.onBackground.adjustBrightness(0.7f).toArgb()
        textAlign = Paint.Align.LEFT
        this.textSize = legendTextSize
        isAntiAlias = true
    }


    val barAreas = dataset.points.mapIndexed { index, point ->
        BarArea(
            index = index,
            value = point.value,
            xStart = horizontalPadding + distance.times(index) - distance.div(2),
            xEnd = horizontalPadding + distance.times(index) + distance.div(2)
        )
    }
    var selectedPosition by remember { mutableStateOf(barAreas.first().xStart.plus(1f)) }
    var tempPosition by remember { mutableStateOf(-1000f) }
    val selectedBar by remember(selectedPosition, barAreas) {
        derivedStateOf {
            barAreas.find { it.xStart < selectedPosition && selectedPosition < it.xEnd }
        }
    }
    val tempBar by remember(tempPosition, barAreas) {
        derivedStateOf {
            barAreas.find { it.xStart < tempPosition && tempPosition < it.xEnd }
        }
    }

    val scope = rememberCoroutineScope()
    val animatable = remember { Animatable(1f) }
    val tempAnimatable = remember { Animatable(0f) }
    val maxPoint = dataset.points.maxValue()
    val minPoint = dataset.points.minValue()

    LaunchedEffect(key1 = data){
        selectedBar?.index?.let { value ->
            barSelected(dataset.points[value])
        }
    }
    Row(modifier = Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(bottom = 16.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(calculatedWidth)
                    .tapOrPress(
                        onStart = { position ->
                            scope.launch {
                                selectedBar?.let { selected ->
                                    if (position in selected.xStart..selected.xEnd) {
                                        // click in selected area - do nothing
                                    } else {
                                        tempPosition = position
                                        scope.launch {
                                            tempAnimatable.snapTo(0f)
                                            tempAnimatable.animateTo(1f, animationSpec = tween(300))
                                        }
                                    }

                                }
                            }
                        },
                        onCancel = {
                            tempPosition = -Int.MAX_VALUE.toFloat()
                            scope.launch {
                                tempAnimatable.animateTo(0f)
                            }
                        },
                        onCompleted = {
                            val currentSelected = selectedBar
                            scope.launch {
                                selectedPosition = it
                                animatable.snapTo(tempAnimatable.value)
                                selectedBar?.index?.let { value ->
                                    barSelected(dataset.points[value])
                                }
                                async {
                                    animatable.animateTo(
                                        1f,
                                        animationSpec = tween(
                                            300
                                                .times(1f - tempAnimatable.value)
                                                .roundToInt()
                                        )
                                    )
                                }
                                async {
                                    tempAnimatable.snapTo(0f)
                                    currentSelected?.let {
                                        tempPosition = currentSelected.xStart.plus(1f)
                                        tempAnimatable.snapTo(1f)
                                        tempAnimatable.animateTo(0f, tween(300))
                                    }
                                }
                            }
                        }
                    )
            ) {
                val scale = calculateScale((size.height).roundToInt(), dataset.points.map { it.value })
                val chartAreaBottom = size.height - labelSectionHeight
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, size.height - offset ),
                    end = Offset(size.width, size.height - offset)
                )
                barAreas.forEachIndexed { index, item ->
                    val barHeight = item.value.times(scale).toFloat()
                    drawRoundRect(
                        color = dataset.color,
                        topLeft = Offset(
                            x = horizontalPadding + distance.times(index) - barWidth.div(2),
                            y = size.height - barHeight - offset
                        ),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(cornerRadius)
                    )
                    this.drawIntoCanvas { canvas ->
                        val textPositionY = chartAreaBottom - barHeight - smallPadding
                        if(drawValue){
                            canvas.nativeCanvas.drawText(
                                item.value.formattedString(suffix = valueSuffix, useSpacing = true),
                                horizontalPadding + distance.times(index),
                                textPositionY,
                                pointPaint
                            )
                        }
                        canvas.nativeCanvas.drawText(
                            if(formatter!= null) formatter.format(Date(dataset.points[index].timestamp)) else index.toString(),
                            horizontalPadding + distance.times(index),
                            size.height + labelSectionHeight,
                            xAxisPaint
                        )

                    }
                }
                selectedBar?.let { selectedBar ->
                    val yTopLeft = size.height - size.height.times(animatable.value)
                    val ySize =  size.height.minus(smallPadding.times(2))
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(
                                dataset.color.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        topLeft = Offset(
                            x = horizontalPadding + distance.times(selectedBar.index) - selectionWidth.div(
                                2
                            ), y = yTopLeft
                        ),
                        size = Size(selectionWidth, ySize),
                        cornerRadius = CornerRadius(cornerRadius)
                    )
                }
                tempBar?.let { tempBar ->
                    val yTopLeft = size.height + smallPadding - size.height.times(tempAnimatable.value)
                    val ySize = size.height.minus(smallPadding.times(2))
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(
                                dataset.color.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        topLeft = Offset(
                            x = horizontalPadding + distance.times(tempBar.index) - selectionWidth.div(
                                2
                            ), y = yTopLeft
                        ),
                        size = Size(selectionWidth, ySize),
                        cornerRadius = CornerRadius(cornerRadius)
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(80.dp)
                .padding(start = 8.dp, bottom = 16.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()){
                drawLine(
                    color = Color.Gray,
                    start = Offset(-smallPadding.times(2), 0f),
                    end = Offset(-smallPadding.times(2), size.height)
                )
                offset = size.height.times(abs(minPoint)).div((maxPoint - minPoint)).toFloat()
                this.drawIntoCanvas { canvas ->
                    if(maxPoint >= 0.0){
                        canvas.nativeCanvas.drawText(
                            maxPoint.formattedString(suffix = valueSuffix, useSpacing = true, noDecimal = true),
                            0f,
                            0f,
                            valuePaint
                        )
                        if(dataset.maxValueDescription != ""){
                            canvas.nativeCanvas.drawText(
                                dataset.maxValueDescription,
                                0f,
                                smallPadding + textSize,
                                descPaint
                            )
                        }
                    }
                    canvas.nativeCanvas.drawText(
                        (0.0).formattedString(suffix = valueSuffix, useSpacing = true, noDecimal = true),
                        0f,
                        size.height - offset,
                        valuePaint
                    )
                    if(minPoint <= 0.0){
                        val yPos = if(dataset.minValueDescription == "") size.height else size.height - (smallPadding + textSize)
                        canvas.nativeCanvas.drawText(
                            minPoint.formattedString(suffix = valueSuffix, useSpacing = true, noDecimal = true),
                            0f,
                            yPos,
                            valuePaint
                        )
                        if(dataset.minValueDescription != ""){
                            canvas.nativeCanvas.drawText(
                                dataset.maxValueDescription,
                                0f,
                                size.height,
                                descPaint
                            )
                        }
                    }
                }
            }
        }
    }
}

fun calculateScale(viewHeightPx: Int, values: List<Double>): Double {
    val max = values.maxOrNull()?:1.0
    val min = values.minOrNull()?:0.0

    return viewHeightPx.div(max - min)
}

data class BarArea(
    val index: Int,
    val xStart: Float,
    val xEnd: Float,
    val value: Double
)
