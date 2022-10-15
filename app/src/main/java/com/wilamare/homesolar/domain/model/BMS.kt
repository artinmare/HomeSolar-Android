package com.wilamare.homesolar.domain.model

import com.wilamare.homesolar.domain.battery.Cell

data class BMS(
    val voltage: Double = 0.0,
    val amperage: Double = 0.0,
    val power: Int = 0,
    val soc: Int = 0,
    val status: String = "Unknown",
    val balance: String = "Unknown",
    val cells: List<Cell> = emptyList(),
)
