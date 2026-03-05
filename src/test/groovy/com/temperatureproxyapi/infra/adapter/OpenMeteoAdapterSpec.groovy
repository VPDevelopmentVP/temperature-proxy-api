package com.temperatureproxyapi.infra.adapter

import com.temperatureproxyapi.domain.model.CurrentConditions
import com.temperatureproxyapi.domain.model.Location
import com.temperatureproxyapi.domain.model.TemperatureData
import com.github.benmanes.caffeine.cache.Caffeine
import com.temperatureproxyapi.infra.adapter.OpenMeteoAdapter
import com.temperatureproxyapi.infra.weather.client.OpenMeteoClient
import com.temperatureproxyapi.infra.weather.client.dto.OpenMeteoCurrent
import com.temperatureproxyapi.infra.weather.client.dto.OpenMeteoResponse
import com.temperatureproxyapi.infra.weather.mapper.OpenMeteoMapper
import spock.lang.Specification

import java.time.Instant

class OpenMeteoAdapterSpec extends Specification {

    def client = Mock(OpenMeteoClient)
    def mapper = Mock(OpenMeteoMapper)
    def cache = Caffeine.newBuilder().build()
    def adapter = new OpenMeteoAdapter(client, mapper, cache)

    def setup() {
        cache.invalidateAll()
    }

    def "should fetch data via client, pass to mapper, and return its result"() {
        given:
        def location = new Location(52.52, 13.41)
        def clientResponse = new OpenMeteoResponse(
            52.52, 13.41,
            new OpenMeteoCurrent("2026-01-01T12:00", 5.3, 15.0)
        )
        def expected = new TemperatureData(
            location,
            new CurrentConditions(5.3, 15.0),
            "open-meteo",
            Instant.now()
        )

        when:
        def result = adapter.fetchTemperature(location)

        then:
        1 * client.fetch(location.lat, location.lon) >> clientResponse
        1 * mapper.toDomain(clientResponse, _ as Instant) >> expected
        result == expected
    }

    def "should propagate exceptions from client"() {
        given:
        def location = new Location(0.0, 0.0)
        client.fetch(0.0, 0.0) >> { throw new RuntimeException("Network error") }

        when:
        adapter.fetchTemperature(location)

        then:
        thrown(RuntimeException)
    }
}
