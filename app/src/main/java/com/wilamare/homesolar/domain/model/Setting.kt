package com.wilamare.homesolar.domain.model

import com.wilamare.homesolar.domain.setting.Condition

data class Setting(
    val conditions: List<Condition> = emptyList()
)
