package de.bebauer.webflux.handler.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters.fromObject
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class CompletionsTests {

    @Test
    fun `ok without body`() {
        runHandlerTest(handler {
            complete()
        }, { expectStatus().isOk.expectBody().isEmpty })
    }

    @Test
    fun `status without body`() {
        runHandlerTest(handler {
            complete(HttpStatus.UNAUTHORIZED)
        }, { expectStatus().isUnauthorized.expectBody().isEmpty })
    }

    @Test
    fun `complete with mono`() {
        runHandlerTest(
            handler {
                complete(Mono.just("xxx"))
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("xxx") }
            })
    }

    @Test
    fun `complete with flux`() {
        runHandlerTest(
            handler {
                complete(Flux.fromIterable(listOf(1, 2)))
            },
            {
                expectStatus().isOk.expectBodyList(Int::class.java).returnResult()
                    .apply { assertThat(responseBody).containsExactly(1, 2) }
            })
    }

    @Test
    fun `complete with value`() {
        runHandlerTest(
            handler {
                complete("xxx")
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("xxx") }
            })
    }

    @Test
    fun `complete with status and mono`() {
        runHandlerTest(
            handler {
                complete(HttpStatus.UNAUTHORIZED, Mono.just("xxx"))
            },
            {
                expectStatus().isUnauthorized.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("xxx") }
            })
    }

    @Test
    fun `complete with status and flux`() {
        runHandlerTest(
            handler {
                complete(HttpStatus.UNAUTHORIZED, Flux.fromIterable(listOf(1, 2)))
            },
            {
                expectStatus().isUnauthorized.expectBodyList(Int::class.java).returnResult()
                    .apply { assertThat(responseBody).containsExactly(1, 2) }
            })
    }

    @Test
    fun `complete with status and value`() {
        runHandlerTest(
            handler {
                complete(HttpStatus.UNAUTHORIZED, "xxx")
            },
            {
                expectStatus().isUnauthorized.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("xxx") }
            })
    }

    @Test
    fun `complete with builder`() {
        runHandlerTest(
            handler {
                complete {
                    contentType(MediaType.APPLICATION_JSON)
                    header("xxx", "abc")
                    body(fromObject("123"))
                }
            },
            {
                expectStatus().isOk
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectHeader().valueEquals("xxx", "abc")
                    .expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("123") }
            })
    }

    @Test
    fun `complete with status and builder`() {
        runHandlerTest(
            handler {
                complete(HttpStatus.UNAUTHORIZED) {
                    contentType(MediaType.APPLICATION_JSON)
                    header("xxx", "abc")
                    body(fromObject("123"))
                }
            },
            {
                expectStatus().isUnauthorized
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectHeader().valueEquals("xxx", "abc")
                    .expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("123") }
            })
    }

    @Test
    fun `complete with body inserter`() {
        runHandlerTest(
            handler {
                complete(fromObject("123"))
            },
            {
                expectStatus().isOk
                    .expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("123") }
            })
    }

    @Test
    fun `complete with status and body inserter`() {
        runHandlerTest(
            handler {
                complete(HttpStatus.UNAUTHORIZED, fromObject("123"))
            },
            {
                expectStatus().isUnauthorized
                    .expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("123") }
            })
    }
}