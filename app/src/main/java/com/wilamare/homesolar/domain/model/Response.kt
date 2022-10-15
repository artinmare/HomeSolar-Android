package com.wilamare.homesolar.domain.model

import com.wilamare.homesolar.presentation.statistic.StatisticData

data class Response(
    val homeDataset: Dataset = Dataset(name = StatisticData.HOME),
    val solarDataset: Dataset = Dataset(name = StatisticData.SOLAR),
    val batteryDataset: Dataset = Dataset(name = StatisticData.BATTERY),
    val inverterDataset: Dataset = Dataset(name = StatisticData.INVERTER),
    val gridDataset: Dataset = Dataset(name = StatisticData.GRID),
    val socData: List<Data> = emptyList()
)

data class Dataset(
    val name: StatisticData,
    val data: List<Data> = emptyList()
)

data class Data(
    val timestamp: Long,
    val value: Double,
)