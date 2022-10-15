package com.wilamare.homesolar.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*

@Composable
fun BasicTextBox(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 8.dp,
    title: String,
    subtitle: String? = null,
    description: String? = null,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    Card(modifier = modifier) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.h6, fontSize = fontSize)
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.body1,
                    fontSize = fontSize.minus(1.sp)
                )
            }
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.body2,
                    fontSize = fontSize.minus(2.sp)
                )
            }
        }
    }
}

@Preview
@Composable
fun BasicTextBoxPrev() {
    BasicTextBox(title = "Highest Voltage", subtitle = "3.456 V", description = "[Cell 13]")
}