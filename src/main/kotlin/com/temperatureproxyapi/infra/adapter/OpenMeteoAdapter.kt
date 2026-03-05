package com.temperatureproxyapi.infra.adapter

import com.github.benmanes.caffeine.cache.Cache
import com.temperatureproxyapi.domain.model.Location
import com.temperatureproxyapi.domain.model.TemperatureData
import com.temperatureproxyapi.domain.port.WeatherPort
import com.temperatureproxyapi.infra.weather.client.OpenMeteoClient
import com.temperatureproxyapi.infra.weather.mapper.OpenMeteoMapper
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class OpenMeteoAdapter(
    private val client: OpenMeteoClient,
    private val mapper: OpenMeteoMapper,
    private val cache: Cache<String, TemperatureData>
) : WeatherPort {

    override fun fetchTemperature(location: Location): TemperatureData =
        cache.get(cacheKey(location)) {
            val response = client.fetch(location.lat, location.lon)
            mapper.toDomain(response, Instant.now())
        }

    private fun cacheKey(location: Location) = "${location.lat}:${location.lon}"
}
