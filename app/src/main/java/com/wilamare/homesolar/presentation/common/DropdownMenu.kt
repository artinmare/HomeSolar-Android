package com.wilamare.homesolar.presentation.common

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.androidpoet.dropdown.*
import com.wilamare.homesolar.ui.theme.Teal200

@ExperimentalAnimationApi
@Composable
fun Menu(modifier:Modifier = Modifier,isOpen: Boolean = false, offset: DpOffset = DpOffset(8.dp, 0.dp), setIsOpen: (Boolean) -> Unit, menu:MenuItem<String>, itemSelected: (String) -> Unit) {
    Dropdown(
        modifier = modifier,
        isOpen = isOpen,
        menu = menu,
        colors = dropDownMenuColors(Teal200, Color.White),
        onItemSelected = itemSelected,
        onDismiss = { setIsOpen(false) },
        offset = offset,
        enter = EnterAnimation.ElevationScale,
        exit = ExitAnimation.ElevationScale,
        easing = Easing.FastOutSlowInEasing,
        enterDuration = 400,
        exitDuration = 400

    )
}