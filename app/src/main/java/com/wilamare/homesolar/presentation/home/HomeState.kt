package com.wilamare.homesolar.presentation.home

data class HomeState(
    val todayGeneration: Double = 0.0,
    val todayIndependence: Int = 0,
    val solar: Double = 0.0,
    val status: String = "Standby",
    val battery: Double = 0.0,
    val charge: Int = 0,
    val inverter: Double = 0.0,
    val grid: Double = 0.0,
    val home: Double = 0.0
)
