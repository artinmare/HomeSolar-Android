package com.wilamare.homesolar.presentation.battery

import com.wilamare.homesolar.domain.battery.Cell

data class BatteryState(
    val voltage: Double = 0.0,
    val amperage: Double = 0.0,
    val power: Double = 0.0,
    val charge: Double = 0.0,
    val status: String = "Unknown",
    val balance: String = "Unknown",
    val cells: List<Cell> = emptyList()
)
