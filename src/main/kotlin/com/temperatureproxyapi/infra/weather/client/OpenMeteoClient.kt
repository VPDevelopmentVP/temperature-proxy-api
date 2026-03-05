package com.temperatureproxyapi.infra.weather.client

import com.temperatureproxyapi.infra.weather.client.dto.OpenMeteoResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class OpenMeteoClient(private val openMeteoRestClient: RestClient) {

    fun fetch(lat: Double, lon: Double): OpenMeteoResponse =
        openMeteoRestClient.get()
            .uri { builder ->
                builder.path("/v1/forecast")
                    .queryParam("latitude", lat)
                    .queryParam("longitude", lon)
                    .queryParam("current", CURRENT_FIELDS)
                    .build()
            }
            .retrieve()
            .body<OpenMeteoResponse>()!!


    private companion object {
        const val CURRENT_FIELDS = "temperature_2m,wind_speed_10m"
    }
}
