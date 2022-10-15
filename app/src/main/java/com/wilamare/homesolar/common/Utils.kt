package com.wilamare.homesolar.common

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import com.wilamare.homesolar.presentation.common.ChartData
import kotlinx.coroutines.coroutineScope
import kotlin.math.roundToInt

fun Double.formattedString(suffix:String = "", noDecimal:Boolean = true, useSpacing: Boolean = false): String {
    val spacing = if(useSpacing) " " else ""
    if(this >= 1000000.0)
        return String.format("%.2f", this/1000000.0) + "${spacing}M$suffix"
    else if(this >= 1000.0)
        return String.format("%.2f", this/1000.0) + "${spacing}k$suffix"
    return if(noDecimal) this.roundToInt().toString() + "${spacing}$suffix" else String.format("%.2f", this) + "${spacing}k$suffix"
}

fun Int.formattedString(suffix:String = "", noDecimal:Boolean = true, useSpacing: Boolean = false): String {
    val spacing = if(useSpacing) " " else ""
    if(this >= 1000000)
        return String.format("%.2f", this/1000000) + "${spacing}M$suffix"
    else if(this >= 1000)
        return String.format("%.2f", this/1000) + "${spacing}k$suffix"
    return if(noDecimal) this.toString() + "${spacing}$suffix" else String.format("%.2f", this) + "${spacing}k$suffix"
}

fun Color.adjustBrightness(brightness: Float = 0.5f): Color {
    return Color(android.graphics.Color.HSVToColor(FloatArray(3).apply {
        android.graphics.Color.colorToHSV(this@adjustBrightness.toArgb(), this)
        this[2] *= brightness
    }))
}

fun String.addCharAtIndex(char: Char, index: Int) =
    StringBuilder(this).apply { insert(index, char) }.toString()

fun List<ChartData>.getTotalValue():Double{
    var total = 0.0
    this.forEach {
        total += it.value
    }
    return  total
}

fun Modifier.tapOrPress(
    onStart: (offsetX: Float) -> Unit,
    onCancel: (offsetX: Float) -> Unit,
    onCompleted: (offsetX: Float) -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    this.pointerInput(interactionSource) {
        forEachGesture {
            coroutineScope {
                awaitPointerEventScope {
                    val tap = awaitFirstDown()
                        .also { if (it.pressed != it.previousPressed) it.consume() }
                    onStart(tap.position.x)
                    val up = waitForUpOrCancellation()
                    if (up == null) {
                        onCancel(tap.position.x)
                    } else {
                        if (up.pressed != up.previousPressed) up.consume()
                        onCompleted(tap.position.x)
                    }
                }
            }
        }
    }
}
