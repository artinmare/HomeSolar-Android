package com.wilamare.homesolar.presentation.common

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

operator fun TextUnit.minus(textUnit: TextUnit): TextUnit {
    if(this@minus.isEm && textUnit.isEm){
        return (this@minus.value - textUnit.value).em
    }
    if(this@minus.isSp && textUnit.isSp){
        return (this@minus.value - textUnit.value).sp
    }
    return this@minus
}