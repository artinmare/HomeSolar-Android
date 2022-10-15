package com.wilamare.homesolar.presentation.setting

import com.wilamare.homesolar.domain.setting.Action
import com.wilamare.homesolar.domain.setting.Condition
import com.wilamare.homesolar.domain.setting.Parameter

data class SettingState(
    val conditions: List<Condition> = emptyList()
)

//data class SettingState(
//    val conditions: List<Condition> = listOf(
//        Condition( name = "Switch to Off-Grid", description = "Switch to Off-Grid if the Battery is almost full",
//            parameters = listOf(
//                Parameter(measurement = "BMS", field = "SoC", value = "90")
//            ),
//            actions = listOf(Action(type = "GPIO"))
//        ),
//        Condition( name = "Switch to Grid", description = "Switch to Grid if the Battery is at 70% and Grid is online",
//            parameters = listOf(
//                Parameter(measurement = "BMS", field = "SoC", operator = "<=", value = "70", type = "AND"),
//                Parameter(measurement = "Pi-Board", field = "GPIO4", operator = "==", value = "ON")
//            ),
//            actions = listOf(Action(type = "GPIO"))
//        ),
//        Condition( name = "Switch Front OFF", description = "Switch Front House Load to OFF if Battery is at 50% and Grid is offline",
//            parameters = listOf(
//                Parameter(measurement = "BMS", field = "SoC", operator = "<=", value = "50", type = "AND"),
//                Parameter(measurement = "Pi-Board", field = "GPIO4", operator = "==", value = "OFF")
//            ),
//            actions = listOf(Action(type = "GPIO"))
//        ),
//        Condition( name = "Switch Front ON", description = "Switch Front House Load to ON if Battery is at 60% or Grid is online",
//            parameters = listOf(
//                Parameter(measurement = "BMS", field = "SoC", operator = ">=", value = "60", type = "OR"),
//                Parameter(measurement = "Pi-Board", field = "GPIO4", operator = "==", value = "ON")
//            ),
//            actions = listOf(Action(type = "GPIO"))
//        ),
//        Condition( name = "Switch Middle and Back OFF", description = "Switch Middle and Back House Load to OFF if Battery is at 40% and Grid is offline",
//            parameters = listOf(
//                Parameter(measurement = "BMS", field = "SoC", operator = "<=", value = "40", type = "AND"),
//                Parameter(measurement = "Pi-Board", field = "GPIO4", operator = "==", value = "OFF")
//            ),
//            actions = listOf(Action(type = "GPIO"),Action(type = "GPIO"))
//        ),
//        Condition( name = "Switch Middle and Back ON", description = "Switch Middle and Back House Load to ON if Battery is at 50% or Grid is online",
//            parameters = listOf(
//                Parameter(measurement = "BMS", field = "SoC", operator = ">=", value = "50", type = "OR"),
//                Parameter(measurement = "Pi-Board", field = "GPIO4", operator = "==", value = "ON")
//            ),
//            actions = listOf(Action(type = "GPIO"),Action(type = "GPIO"))
//        )
//    )
//)