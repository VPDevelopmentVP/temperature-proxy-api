package com.temperatureproxyapi.domain.service

import com.temperatureproxyapi.domain.model.CurrentConditions
import com.temperatureproxyapi.domain.model.Location
import com.temperatureproxyapi.domain.model.TemperatureData
import com.temperatureproxyapi.domain.port.WeatherPort
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant

class TemperatureServiceSpec extends Specification {

    def weatherPort = Mock(WeatherPort)
    def service = new TemperatureService(weatherPort)

    @Unroll
    def "should return temperature data for coordinates: lat=#lat, lon=#lon"() {
        given:
        def location = new Location(lat, lon)
        def currCondition = new CurrentConditions(1.2, 9.7)

        def expected = new TemperatureData(location, currCondition, "open-meteo", Instant.now())

        when:
        def result = service.getTemperature(lat, lon)

        then:
        1 * weatherPort.fetchTemperature(new Location(lat, lon)) >> expected
        result == expected

        where:
        lat   | lon
        52.52 | 13.41
        90.0  | 180.0
        -90.0 | -180.0
        0.0   | 0.0
    }

    @Unroll
    def "should reject latitude=#lat out of range"() {
        given:
        def expected = "Latitude"

        when:
        service.getTemperature(lat, 0.0)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message.contains(expected)
        0 * weatherPort.fetchTemperature(_)

        where:
        lat   | _
        90.1  | _
        -90.1 | _
        180.0 | _
    }

    @Unroll
    def "should reject longitude=#lon out of range"() {
        given:
        def expected = "Longitude"

        when:
        service.getTemperature(0.0, lon)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message.contains(expected)
        0 * weatherPort.fetchTemperature(_)

        where:
        lon    | _
        180.1  | _
        -180.1 | _
        360.0  | _
    }
}
