package com.temperatureproxyapi.application.mapper

import com.temperatureproxyapi.application.dto.CurrentConditionsDto
import com.temperatureproxyapi.application.dto.LocationDto
import com.temperatureproxyapi.application.dto.TemperatureResponse
import com.temperatureproxyapi.domain.model.TemperatureData
import org.springframework.stereotype.Component

@Component
class TemperatureResponseMapper {

    fun toResponse(data: TemperatureData): TemperatureResponse =
        TemperatureResponse(
            location = LocationDto(lat = data.location.lat, lon = data.location.lon),
            current = CurrentConditionsDto(
                temperatureC = data.current.temperatureC,
                windSpeedKmh = data.current.windSpeedKmh
            ),
            source = data.source,
            retrievedAt = data.retrievedAt
        )
}
