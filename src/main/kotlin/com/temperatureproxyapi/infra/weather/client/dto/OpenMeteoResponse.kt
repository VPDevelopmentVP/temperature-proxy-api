package com.temperatureproxyapi.infra.weather.client.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class OpenMeteoResponse(
    val latitude: Double,
    val longitude: Double,
    val current: OpenMeteoCurrent
)

data class OpenMeteoCurrent(
    val time: String,
    @JsonProperty("temperature_2m") val temperature2m: Double,
    @JsonProperty("wind_speed_10m") val windSpeed10m: Double
)
