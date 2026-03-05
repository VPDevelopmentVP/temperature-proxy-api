package com.temperatureproxyapi.domain.service

import com.temperatureproxyapi.domain.model.Location
import com.temperatureproxyapi.domain.model.TemperatureData
import com.temperatureproxyapi.domain.port.WeatherPort
import org.springframework.stereotype.Service

@Service
class TemperatureService(private val weatherPort: WeatherPort) {

    fun getTemperature(lat: Double, lon: Double): TemperatureData {
        require(lat in -90.0..90.0) { "Latitude must be between -90 and 90, got $lat" }
        require(lon in -180.0..180.0) { "Longitude must be between -180 and 180, got $lon" }
        return weatherPort.fetchTemperature(Location(lat, lon))
    }
}
