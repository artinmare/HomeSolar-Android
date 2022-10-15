package com.wilamare.homesolar.presentation.common


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wilamare.homesolar.common.adjustBrightness
import com.wilamare.homesolar.domain.battery.Cell
import com.wilamare.homesolar.ui.theme.PrimaryGreen
import com.wilamare.homesolar.ui.theme.PrimaryRed
import com.wilamare.homesolar.ui.theme.PrimaryYellow
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CellItem(
    modifier: Modifier = Modifier,
    cell: Cell
) {
    val borderColor = MaterialTheme.colors.onSurface.adjustBrightness(0.95f)
    var currentPercentage by remember { mutableStateOf(0f) }
    val percentage = remember {
        Animatable(initialValue = 0f)
    }

    LaunchedEffect(cell.value) {
        currentPercentage = calculatePercentage(cell.value).toFloat()
        percentage.animateTo(
            targetValue = currentPercentage,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Column(modifier = modifier) {
        Text(text = cell.title, style = MaterialTheme.typography.body2, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.height(height),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(9f), contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color = calculateColor(percentage.value.roundToInt()),
                        size = size.copy(width = size.width * (percentage.value/100)),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                    drawRoundRect(
                        color = borderColor,
                        cornerRadius = CornerRadius(10f, 10f),
                        style = Stroke(2.dp.toPx())
                    )
                }
                val shadowColor = if(isSystemInDarkTheme()) Color.Black else Color.White
                Text(
                    text = "${String.format(
                        "%.3f",
                        cell.value
                    )} V",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        shadow = Shadow(
                            color = shadowColor,
                            blurRadius = 8f
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Canvas(
                modifier = Modifier
                    .height(height / 2)
                    .weight(1f)
            ) {
                drawCircle(color = borderColor)
                if (cell.isBalanced) {
                    drawCircle(color = Color.Green, radius = (size.minDimension / 2.5f))
                }
            }
        }
    }
    
}

fun List<Double>.closestValue(value: Double) = minByOrNull { abs(value - it) }
private fun calculatePercentage(voltage: Double): Int {
    val y = listOf(
        2.8,
        2.9330377259756997,
        3.0522740648011784,
        3.158464726159795,
        3.2523654197349052,
        3.3347318552098684,
        3.4063197422680425,
        3.467884790592784,
        3.520182709867454,
        3.563969209775406,
        3.6000000000000005,
        3.629030790224596,
        3.6518172901325494,
        3.6691152094072175,
        3.68168025773196,
        3.6902681447901338,
        3.695634580265097,
        3.698535273840207,
        3.6997259351988236,
        3.6999622740243012,
        3.700000000000001,
        3.7004591131259215,
        3.7014167746686306,
        3.702814436211341,
        3.704593549337261,
        3.706695565629603,
        3.709061936671578,
        3.711634114046393,
        3.7143535493372615,
        3.7171616941273937,
        3.7200000000000006,
        3.722822757271723,
        3.7256356111929314,
        3.7284570457474238,
        3.7313055449189996,
        3.734199592691459,
        3.737157673048602,
        3.740198269974228,
        3.7433398674521356,
        3.7466009494661265,
        3.75,
        3.7535398577871875,
        3.757160780559646,
        3.7607873807989693,
        3.764344270986745,
        3.767756063604565,
        3.77094737113402,
        3.7738428060567,
        3.776366980854196,
        3.778444508008099,
        3.7799999999999985,
        3.7810078115795274,
        3.7816412665684815,
        3.7821234310567005,
        3.7826773711340205,
        3.7835261528902793,
        3.7848928424153163,
        3.7870005057989693,
        3.7900722091310746,
        3.7943310185014725,
        3.8,
        3.807218895894698,
        3.815794153166421,
        3.825448894974228,
        3.835906244477173,
        3.8468893248343154,
        3.858121259204714,
        3.869325170747424,
        3.880224182621502,
        3.8905414179860083,
        3.8999999999999995,
        3.9083866048416787,
        3.915742120765832,
        3.922170989046392,
        3.927777650957291,
        3.932666547772459,
        3.9369421207658317,
        3.9407088112113406,
        3.9440710603829157,
        3.947133309554492,
        3.9499999999999993,
        3.9527846847385857,
        3.955637363770251,
        3.9587171488402055,
        3.962183151693667,
        3.966194484075846,
        3.970910257731958,
        3.9764895844072163,
        3.9830915758468337,
        3.9908753437960227,
        3.9999999999999987,
        4.010624656203976,
        4.022908424153167,
        4.037010415592782,
        4.053089742268042,
        4.071305515924153,
        4.091816848306333,
        4.114782851159794,
        4.1403626362297485,
        4.168715315261414,
        4.2
    )
    return y.indexOf(y.closestValue(voltage))
}

private fun calculateColor(percentage: Int): Color {
    return when {
        percentage >= 70 ->
            PrimaryGreen
        percentage in (50..70) ->
            PrimaryYellow
        else ->
            PrimaryRed
    }
}

private val height = 32.dp