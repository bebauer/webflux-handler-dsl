package de.bebauer.webflux.handler.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

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

    @Test
    fun `execute sub DSL ok`() {
        runHandlerTest(
            handler {
                val result = execute {
                    complete("Test")
                }

                complete(result.flatMap { ServerResponse.from(it).body(fromObject("Yay!")) })
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("Yay!") }
            })
    }

    @Test
    fun `execute sub DSL failure`() {
        runHandlerTest(
            handler {
                val result = execute {
                    failWith("Test")
                }

                complete(result.onErrorResume { ServerResponse.ok().body(fromObject("Nay!")) })
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("Nay!") }
            })
    }

    @Test
    fun `complete with Either`() {
        runHandlerTest(
            handler {
                val result = execute {
                    complete("Test")
                }

                complete(result)
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("Test") }
            })
    }

    @Test
    fun `alternative completions last`() {
        runHandlerTest(
            handler {
                complete(Mono.empty()) or complete(Mono.empty<String>()) or complete(HttpStatus.NOT_FOUND, "Test")
            },
            {
                expectStatus().isNotFound.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("Test") }
            })
    }

    @Test
    fun `alternative completions first`() {
        runHandlerTest(
            handler {
                complete("1") or complete(HttpStatus.NOT_FOUND, "1")
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("1") }
            })
    }

    @Test
    fun `alternative failWith`() {
        runHandlerTest(
            handler {
                complete(Mono.empty()) or failWith("Error!")
            },
            {
                expectStatus().is5xxServerError
            })
    }
}