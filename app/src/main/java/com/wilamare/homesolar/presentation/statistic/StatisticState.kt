package com.wilamare.homesolar.presentation.statistic

import com.wilamare.homesolar.domain.model.Response
import com.wilamare.homesolar.presentation.common.ChartData
import java.util.*

data class StatisticState(
    val selectedData: StatisticData = StatisticData.HOME,
    val timescale: Timescale = Timescale.DAY,
    val selectedDate: Date = Date(),
    val datasets: Response = Response()
)

enum class StatisticData{
    HOME, SOLAR, BATTERY, INVERTER, GRID
}

enum class Timescale{
    DAY, MONTH, YEAR
}

data class StatisticChartState(
    val isAggregated: Boolean = false,
    val zeroOffset: Float = 0f,
    val chartType: ChartType = ChartType.LINE,
    val selectedPoint: ChartData = ChartData(0,0.0)
)

enum class ChartType {
    LINE, BAR
}
