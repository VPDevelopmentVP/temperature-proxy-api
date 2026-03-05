package com.temperatureproxyapi.infra.adapter

import com.github.benmanes.caffeine.cache.Caffeine
import com.temperatureproxyapi.domain.model.CurrentConditions
import com.temperatureproxyapi.domain.model.Location
import com.temperatureproxyapi.domain.model.TemperatureData
import com.temperatureproxyapi.infra.weather.client.OpenMeteoClient
import com.temperatureproxyapi.infra.weather.client.dto.OpenMeteoCurrent
import com.temperatureproxyapi.infra.weather.client.dto.OpenMeteoResponse
import com.temperatureproxyapi.infra.weather.mapper.OpenMeteoMapper
import spock.lang.Specification

import java.time.Instant
import java.util.concurrent.TimeUnit

class OpenMeteoAdapterCacheSpec extends Specification {

    def client = Mock(OpenMeteoClient)
    def mapper = Mock(OpenMeteoMapper)
    def cache = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .maximumSize(1000)
            .build()
    def adapter = new OpenMeteoAdapter(client, mapper, cache)

    def setup() {
        cache.invalidateAll()
    }

    def "second call for same location is served from cache — upstream called only once"() {
        given:
        def location = new Location(52.52, 13.41)
        def response = new OpenMeteoResponse(52.52, 13.41,
                new OpenMeteoCurrent("2026-01-01T12:00", 5.3, 15.0))
        def expected = new TemperatureData(location,
                new CurrentConditions(5.3, 15.0), "open-meteo", Instant.now())

        when:
        def first  = adapter.fetchTemperature(location)
        def second = adapter.fetchTemperature(location)

        then:
        1 * client.fetch(52.52, 13.41) >> response
        1 * mapper.toDomain(response, _ as Instant) >> expected
        first == expected
        second == expected
        first.is(second)
    }

    def "different locations have independent cache entries"() {
        given:
        def berlin = new Location(52.52, 13.41)
        def paris  = new Location(48.85, 2.35)

        def berlinResponse = new OpenMeteoResponse(52.52, 13.41,
                new OpenMeteoCurrent("2026-01-01T12:00", 5.3, 15.0))
        def parisResponse  = new OpenMeteoResponse(48.85, 2.35,
                new OpenMeteoCurrent("2026-01-01T12:00", 10.1, 20.0))

        def berlinData = new TemperatureData(berlin,
                new CurrentConditions(5.3, 15.0), "open-meteo", Instant.now())
        def parisData  = new TemperatureData(paris,
                new CurrentConditions(10.1, 20.0), "open-meteo", Instant.now())

        when:
        def result1 = adapter.fetchTemperature(berlin)
        def result2 = adapter.fetchTemperature(paris)

        then:
        1 * client.fetch(52.52, 13.41) >> berlinResponse
        1 * mapper.toDomain(berlinResponse, _ as Instant) >> berlinData
        1 * client.fetch(48.85, 2.35) >> parisResponse
        1 * mapper.toDomain(parisResponse, _ as Instant) >> parisData
        result1 == berlinData
        result2 == parisData
    }

    def "cache invalidation forces a fresh upstream call"() {
        given:
        def location = new Location(52.52, 13.41)
        def response = new OpenMeteoResponse(52.52, 13.41,
                new OpenMeteoCurrent("2026-01-01T12:00", 5.3, 15.0))
        def data = new TemperatureData(location,
                new CurrentConditions(5.3, 15.0), "open-meteo", Instant.now())

        when:
        adapter.fetchTemperature(location)
        cache.invalidateAll()
        adapter.fetchTemperature(location)

        then:
        2 * client.fetch(52.52, 13.41) >> response
        2 * mapper.toDomain(response, _ as Instant) >> data
    }
}
