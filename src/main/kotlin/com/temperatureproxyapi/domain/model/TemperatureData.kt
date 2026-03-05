package com.temperatureproxyapi.domain.model

import java.time.Instant


data class TemperatureData(
    val location: Location,
    val current: CurrentConditions,
    val source: String,
    val retrievedAt: Instant
)
