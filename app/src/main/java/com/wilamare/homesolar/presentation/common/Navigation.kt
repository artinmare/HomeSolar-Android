package com.wilamare.homesolar.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wilamare.homesolar.R
import com.wilamare.homesolar.common.adjustBrightness
import com.wilamare.homesolar.domain.common.MenuItem

@Composable
fun DrawerHeader(user: String = "user", backgroundColor: Color = MaterialTheme.colors.primary) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = mutableListOf(
                        backgroundColor,
                        backgroundColor.adjustBrightness(0.95f),
                        backgroundColor.adjustBrightness(0.9f),
                        backgroundColor.adjustBrightness(0.85f),
                        backgroundColor.adjustBrightness(0.6f),
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.onPrimary
        )
        Text(
            text = "Hi $user",
            color = MaterialTheme.colors.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NavigationItem(onClick: () -> Unit, title: String, desc: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 32.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = title, fontSize = 24.sp)
            desc?.let {
                Text(text = it, fontSize = 16.sp, fontWeight = FontWeight.Light)
            }
        }
        Column {
            Text(text = "", fontSize = 24.sp)
            Text(text = "", fontSize = 16.sp, fontWeight = FontWeight.Light)
        }
        Icon(imageVector = Icons.Default.ArrowForwardIos, contentDescription = "Forward Arrow")
    }
}

@Composable
fun DrawerBody(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (MenuItem) -> Unit
) {
    LazyColumn(modifier) {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick(item)
                    }
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.contentDescription
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    style = itemTextStyle,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
