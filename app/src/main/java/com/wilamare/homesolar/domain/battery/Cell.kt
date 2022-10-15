package com.wilamare.homesolar.domain.battery

data class Cell(
    val title: String,
    val value: Double,
    val isBalanced: Boolean = false
)
