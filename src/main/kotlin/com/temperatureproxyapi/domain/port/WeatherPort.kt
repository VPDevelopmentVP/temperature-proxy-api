package com.temperatureproxyapi.domain.port

import com.temperatureproxyapi.domain.model.Location
import com.temperatureproxyapi.domain.model.TemperatureData

interface WeatherPort {
    fun fetchTemperature(location: Location): TemperatureData
}
