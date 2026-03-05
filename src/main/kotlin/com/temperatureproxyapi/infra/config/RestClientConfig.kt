package com.temperatureproxyapi.infra.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.net.http.HttpClient
import java.time.Duration

@Configuration
class RestClientConfig(
    @Value("\${weather.upstream.base-url:https://api.open-meteo.com}") private val baseUrl: String,
    @Value("\${weather.upstream.timeout-ms:1000}") private val timeoutMs: Long
) {

    @Bean
    fun openMeteoRestClient(): RestClient {
        val httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(timeoutMs))
            .build()

        val factory = JdkClientHttpRequestFactory(httpClient)
        factory.setReadTimeout(Duration.ofMillis(timeoutMs))

        return RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(factory)
            .build()
    }
}
