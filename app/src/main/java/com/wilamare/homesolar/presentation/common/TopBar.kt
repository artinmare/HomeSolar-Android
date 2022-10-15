package com.wilamare.homesolar.presentation.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wilamare.homesolar.R

@Composable
fun TopBar(onNavigationIconClick: () -> Unit = {}, icon: ImageVector? = null, contentDescription: String? = null, title: String = stringResource(id = R.string.app_name)) {
    TopAppBar(
        title =  {
            Text(text = title)
        },
        navigationIcon = {
            icon?.let {
                IconButton(onClick = onNavigationIconClick) {
                    Icon(imageVector = it, contentDescription = contentDescription)
                }
            }
        }
    )
}

@Preview
@Composable
fun TopBarPrev() {
    TopBar()
}

