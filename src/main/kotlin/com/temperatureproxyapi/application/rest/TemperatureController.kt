package com.temperatureproxyapi.application.rest

import com.temperatureproxyapi.application.dto.TemperatureResponse
import com.temperatureproxyapi.application.mapper.TemperatureResponseMapper
import com.temperatureproxyapi.domain.service.TemperatureService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/temperature")
@Validated
@Tag(name = "Temperature", description = "Current weather conditions API")
class TemperatureController(
    private val temperatureService: TemperatureService,
    private val mapper: TemperatureResponseMapper
) {

    @GetMapping
    @Operation(
        summary = "Get current temperature",
        description = "Returns current temperature and wind speed for the specified coordinates. Results are cached for 60 seconds."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Weather data retrieved successfully",
        content = [Content(schema = Schema(implementation = TemperatureResponse::class))]
    )
    @ApiResponse(responseCode = "400", description = "Invalid latitude or longitude")
    @ApiResponse(responseCode = "503", description = "Upstream weather service unavailable or timed out")
    fun getTemperature(
        @Parameter(description = "Latitude in decimal degrees (-90 to 90)")
        @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
        @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
        @RequestParam
        lat: Double,

        @Parameter(description = "Longitude in decimal degrees (-180 to 180)")
        @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
        @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
        @RequestParam
        lon: Double
    ): ResponseEntity<TemperatureResponse> {
        val data = temperatureService.getTemperature(lat, lon)
        return ResponseEntity.ok(mapper.toResponse(data))
    }
}