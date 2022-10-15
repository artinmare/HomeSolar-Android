package com.wilamare.homesolar.presentation.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextWithDivider(value: String, modifier: Modifier = Modifier, title:String? = null) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
    ) {
        Divider(
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = value)
            title?.let {
                Text(text = it)
            }
        }
    }
}
