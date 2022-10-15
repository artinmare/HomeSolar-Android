package com.wilamare.homesolar.presentation.condition

import com.wilamare.homesolar.domain.setting.Action
import com.wilamare.homesolar.domain.setting.Condition
import com.wilamare.homesolar.domain.setting.Parameter

data class ConditionState(
    val condition: Condition = Condition(),
    val measurements: List<Measurement> = listOf(
        Measurement(
            name = "TasmotaSolar",
            fields = listOf(
                Field(
                    name = "Power",
                    type = DataType.NUMBER
                ),
                Field(
                    name = "Amperage",
                    type = DataType.NUMBER
                ),
                Field(
                    name = "Voltage",
                    type = DataType.NUMBER
                ),
            )
        ),
        Measurement(
            name = "TasmotaInverter",
            fields = listOf(
                Field(
                    name = "Power",
                    type = DataType.NUMBER
                ),
                Field(
                    name = "Amperage",
                    type = DataType.NUMBER
                ),
                Field(
                    name = "Voltage",
                    type = DataType.NUMBER
                ),
            )
        ),
        Measurement(
            name = "TasmotaGrid",
            fields = listOf(
                Field(
                    name = "Power",
                    type = DataType.NUMBER
                ),
                Field(
                    name = "Amperage",
                    type = DataType.NUMBER
                ),
                Field(
                    name = "Voltage",
                    type = DataType.NUMBER
                ),
            )
        ),
        Measurement(
            name = "BMS",
            fields = listOf(
                Field(
                    name = "MOSFET_Discharge",
                    type = DataType.STRING
                ),
                Field(
                    name = "Power",
                    type = DataType.NUMBER
                ),
                Field(
                    name = "Amperage",
                    type = DataType.NUMBER
                ),
                Field(
                    name = "Voltage",
                    type = DataType.NUMBER
                ),
                Field(
                    name = "Cell1",
                    type = DataType.NUMBER
                ),
                Field(
                    name = "Cell2",
                    type = DataType.NUMBER
                ),
                Field(
                    name = "Cell3",
                    type = DataType.NUMBER
                ),
                Field(
                    name = "Cell4",
                    type = DataType.NUMBER
                ),
                Field(
                    name = "Cell5",
                    type = DataType.NUMBER
                ),
                Field(
                    name = "Cell6",
                    type = DataType.NUMBER
                ),
            )
        ),
    ),
)

data class Measurement(
    val name: String,
    val fields: List<Field> = emptyList()
)

data class Field(
    val name: String,
    val type: DataType,
)
enum class DataType{
    NUMBER, STRING
}
enum class DropdownMenu{
    MEASUREMENT, OPERATOR, EXTRA, ACTION, GPIO, GPIO_ACTION
}
