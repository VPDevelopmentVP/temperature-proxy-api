package com.temperatureproxyapi.infra.weather.mapper

import com.temperatureproxyapi.infra.weather.client.dto.OpenMeteoCurrent
import com.temperatureproxyapi.infra.weather.client.dto.OpenMeteoResponse
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant

class OpenMeteoMapperSpec extends Specification {

    def mapper = new OpenMeteoMapper()

    @Unroll
    def "maps #description correctly"() {
        given:
        def retrievedAt = Instant.parse("2026-01-01T12:00:00Z")
        def openMeteoCurrent = new OpenMeteoCurrent("2026-01-01T12:00", temp, wind)
        def response = new OpenMeteoResponse(lat, lon, openMeteoCurrent)

        when:
        def result = mapper.toDomain(response, retrievedAt)

        then:
        with(result) {
            location.lat == lat
            location.lon == lon
            current.temperatureC == temp
            current.windSpeedKmh == wind
            source == OpenMeteoMapper.SOURCE
            it.retrievedAt == retrievedAt
        }

        where:
        description   | lat    | lon     | temp  | wind
        "Warsaw"      | 52.23  | 21.01   | 5.0   | 12.0
        "Sydney"      | -33.87 | 151.21  | 20.0  | 8.5
        "Tokyo"       | 35.68  | 139.69  | 10.0  | 15.0
        "South Pole"  | -90.0  | 0.0     | -60.0 | 20.0
    }
}
