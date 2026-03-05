package com.temperatureproxyapi.infra.weather.mapper

import com.temperatureproxyapi.domain.model.CurrentConditions
import com.temperatureproxyapi.domain.model.Location
import com.temperatureproxyapi.domain.model.TemperatureData
import com.temperatureproxyapi.infra.weather.client.dto.OpenMeteoResponse
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class OpenMeteoMapper {

    fun toDomain(response: OpenMeteoResponse, retrievedAt: Instant): TemperatureData =
        TemperatureData(
            location = Location(response.latitude, response.longitude),
            current = CurrentConditions(
                temperatureC = response.current.temperature2m,
                windSpeedKmh = response.current.windSpeed10m
            ),
            source = SOURCE,
            retrievedAt = retrievedAt
        )

    companion object {
        const val SOURCE = "open-meteo"
    }
}
