package com.temperatureproxyapi.application.dto

import java.time.Instant

data class TemperatureResponse(
    val location: LocationDto,
    val current: CurrentConditionsDto,
    val source: String,
    val retrievedAt: Instant
)

data class LocationDto(val lat: Double, val lon: Double)

data class CurrentConditionsDto(
    val temperatureC: Double,
    val windSpeedKmh: Double
)
