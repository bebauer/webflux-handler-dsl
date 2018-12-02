package de.bebauer.webflux.handler.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class HandlerDslTests {

    @Test
    fun `fail without complete`() {
        runHandlerTest(
            handler { },
            { expectStatus().is5xxServerError })
    }

    @Test
    fun `fail with multiple complete`() {
        runHandlerTest(handler {
            complete("xxx")
            complete("abc")
        }, { expectStatus().is5xxServerError })
    }

    @ParameterizedTest
    @ValueSource(strings = ["a", "b"])
    fun `if - else complete`(value: String) {
        runHandlerTest(
            handler {
                if (value == "a") complete("a") else complete("b")
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo(value) }
            })
    }

    @Test
    fun `explicit failure with exception`() {
        runHandlerTest(
            handler { failWith(ResponseStatusException(HttpStatus.BAD_REQUEST)) },
            { expectStatus().isBadRequest })
    }

    @Test
    fun `explicit failure with error message`() {
        runHandlerTest(
            handler { failWith("Failure") },
            { expectStatus().is5xxServerError })
    }
}