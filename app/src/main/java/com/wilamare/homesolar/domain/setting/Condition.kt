package com.wilamare.homesolar.domain.setting

import com.wilamare.homesolar.presentation.condition.DataType
import kotlinx.serialization.Serializable

@Serializable
data class Condition(
    val name: String = "Condition Name Here",
    val description: String = "You can write your description here",
    val parameters: List<Parameter> = emptyList(),
    val actions: List<Action> = emptyList()
)

@Serializable
data class Parameter(
    val measurement: String = "Measurement#Field",
    val type: DataType = DataType.STRING,
    val operator: String = "==",
    val value: String = "",
    val extra: String = "None",
    val valid: Boolean = true,
)

@Serializable
data class Action(
    val type: String = "GPIO",
    val topic: String = "",
    val payload: String = "{}",
    val gpio: String = "Type#Pin",
    val gpioAction: String = "None",
    val valid: Boolean = true,
)