package com.temperatureproxyapi.application

import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import spock.lang.Specification

class GlobalExceptionHandlerSpec extends Specification {

    def handler = new GlobalExceptionHandler()

    def "handleConstraintViolation returns 400 with all violation messages joined"() {
        given:
        def v1 = Stub(ConstraintViolation) { getMessage() >> "must not be null" }
        def v2 = Stub(ConstraintViolation) { getMessage() >> "must be between -90 and 90" }
        def ex = new ConstraintViolationException("validation failed", [v1, v2] as Set)

        when:
        def response = handler.handleConstraintViolation(ex)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        with(response.body) {
            status == 400
            error == "Bad Request"
            message.contains("must not be null")
            message.contains("must be between -90 and 90")
            timestamp != null
        }
    }

    def "handleTypeMismatch returns 400 with parameter name and received value"() {
        given:
        def ex = Mock(MethodArgumentTypeMismatchException)
        ex.getName() >> "lat"
        ex.getValue() >> "not-a-number"

        when:
        def response = handler.handleTypeMismatch(ex)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        with(response.body) {
            status == 400
            error == "Bad Request"
            message == "Invalid value for parameter 'lat': not-a-number"
            timestamp != null
        }
    }

    def "handleIllegalArgument returns 400 with exception message"() {
        given:
        def ex = new IllegalArgumentException("latitude out of range")

        when:
        def response = handler.handleIllegalArgument(ex)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        with(response.body) {
            status == 400
            error == "Bad Request"
            message == "latitude out of range"
            timestamp != null
        }
    }

    def "handleIllegalArgument returns fallback message when exception message is null"() {
        given:
        def ex = new IllegalArgumentException((String) null)

        when:
        def response = handler.handleIllegalArgument(ex)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        with(response.body) {
            status == 400
            error == "Bad Request"
            message == "Invalid parameters"
            timestamp != null
        }
    }

    def "handleResourceAccess returns 503 with fixed upstream message"() {
        given:
        def ex = new ResourceAccessException("Connection timed out")

        when:
        def response = handler.handleResourceAccess(ex)

        then:
        response.statusCode == HttpStatus.SERVICE_UNAVAILABLE
        with(response.body) {
            status == 503
            error == "Service Unavailable"
            message == "Weather service is unreachable or timed out"
            timestamp != null
        }
    }

    def "handleRestClientResponse returns 502 with fixed gateway message"() {
        given:
        def ex = Mock(RestClientResponseException)

        when:
        def response = handler.handleRestClientResponse(ex)

        then:
        response.statusCode == HttpStatus.BAD_GATEWAY
        with(response.body) {
            status == 502
            error == "Bad Gateway"
            message == "Weather service returned an unexpected error"
            timestamp != null
        }
    }

    def "handleGeneral returns 500 for any unexpected exception"() {
        given:
        def ex = new RuntimeException("something went wrong")

        when:
        def response = handler.handleGeneral(ex)

        then:
        response.statusCode == HttpStatus.INTERNAL_SERVER_ERROR
        with(response.body) {
            status == 500
            error == "Internal Server Error"
            message == "An unexpected error occurred"
            timestamp != null
        }
    }
}
