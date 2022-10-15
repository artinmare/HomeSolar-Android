package com.wilamare.homesolar.data.remote.dto

import com.wilamare.homesolar.domain.model.Summary
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
data class SummaryDto(
    val NowCharge: Double,
    val NowConsumption: Double,
    val NowGrid: Double,
    val NowPercentage: Double,
    val NowProduction: Double,
    val NowSolar: Double,
    val NowTemperature: Int,
    val NowWeather: Int,
    val TodayConsumption: Double,
    val TodayGrid: Double,
    val TodayPercentage: Double,
    val TodayProduction: Double,
    val TodaySolar: Double,
    val TodayTemperature: Int
) {
    fun toSummary(): Summary {
        return Summary(
            solar = NowProduction,
            battery = NowSolar,
            home = NowConsumption,
            grid = NowGrid,
            todayGeneration = TodayProduction,
            todayIndependence = (TodayPercentage*100).roundToInt(),
            charge = NowCharge.roundToInt(),
            status = if(NowSolar == 0.0) 0 else if (NowProduction > NowSolar) 1 else 2
        )
    }
}