package com.temperatureproxyapi.infra.config

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.temperatureproxyapi.domain.model.TemperatureData
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class CacheConfig(
    @Value("\${weather.cache.ttl-seconds:60}") private val cacheTtlSeconds: Long,
    @Value("\${weather.cache.max-size:1000}") private val cacheMaxSize: Long,
    private val meterRegistry: MeterRegistry
) {

    @Bean
    fun temperatureCache(): Cache<String, TemperatureData> {
        val cache = Caffeine.newBuilder()
            .expireAfterWrite(cacheTtlSeconds, TimeUnit.SECONDS)
            .maximumSize(cacheMaxSize)
            .recordStats()
            .build<String, TemperatureData>()
        CaffeineCacheMetrics.monitor(meterRegistry, cache, "temperature")
        return cache
    }
}
