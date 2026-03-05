package com.temperatureproxyapi.application.rest


import com.github.benmanes.caffeine.cache.Cache
import com.temperatureproxyapi.domain.model.TemperatureData
import com.temperatureproxyapi.infra.weather.client.OpenMeteoClient
import com.temperatureproxyapi.infra.weather.client.dto.OpenMeteoCurrent
import com.temperatureproxyapi.infra.weather.client.dto.OpenMeteoResponse
import io.restassured.RestAssured
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TemperatureControllerSpec extends Specification {

    static final String ENDPOINT = "/api/v1/temperature"

    @LocalServerPort
    int port

    @SpringBean
    OpenMeteoClient openMeteoClient = Mock(OpenMeteoClient)

    @Autowired
    Cache<String, TemperatureData> temperatureCache

    def setup() {
        RestAssured.port = port
        temperatureCache.invalidateAll()
    }

    def "returns 200 with correctly mapped body"() {
        given:
        def lat = 52.52
        def lon = 13.41
        def temp = 1.2
        def wind = 9.7
        openMeteoClient.fetch(lat, lon) >> createResponse(lat, lon)

        when:
        def response = RestAssured.given()
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .get(ENDPOINT)

        then:
        with(response){
            statusCode() == HttpStatus.OK.value()
            with(jsonPath()) {
                getJsonObject("location")["lat"] as double == lat
                getJsonObject("location")["lon"] as double == lon
                getJsonObject("current")["temperatureC"] as double == temp
                getJsonObject("current")["windSpeedKmh"] as double == wind
                getJsonObject("source") == "open-meteo"
                getJsonObject("retrievedAt") != null
            }
        }
    }

    def "second request with same coordinates is served from cache"() {
        given:
        def lat = 10.0
        def lon = 20.0

        when: "two identical requests"
        RestAssured.given().queryParam("lat", lat).queryParam("lon", lon).get(ENDPOINT)
        RestAssured.given().queryParam("lat", lat).queryParam("lon", lon).get(ENDPOINT)

        then: "upstream called exactly once — second hit served from Caffeine"
        1 * openMeteoClient.fetch(lat, lon) >> createResponse(lat, lon)
    }

    def "different coordinates bypass each other's cache entry"() {
        given:
        def lat = 10.0
        def lon = 20.0

        when:
        RestAssured.given().queryParam("lat", lat).queryParam("lon", lon).get(ENDPOINT)
        RestAssured.given().queryParam("lat", lat + 1).queryParam("lon", lon + 1).get(ENDPOINT)

        then: "two distinct cache keys → two upstream calls"
        1 * openMeteoClient.fetch(lat, lon) >> createResponse(lat, lon)
        1 * openMeteoClient.fetch(lat + 1, lon + 1) >> createResponse(lat + 1, lon + 1)
    }

    private static OpenMeteoResponse createResponse(double lat, double lon, double temp = 1.2, double wind = 9.7) {
        new OpenMeteoResponse(lat, lon, new OpenMeteoCurrent("2026-01-11T10:12:54", temp, wind))
    }
}
