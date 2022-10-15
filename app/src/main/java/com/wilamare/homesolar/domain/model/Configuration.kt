package com.wilamare.homesolar.domain.model

data class Configuration(
    val solarProduction: Config
)

data class Config(
    val measurement: String,
    val fields: List<String> = emptyList()
)