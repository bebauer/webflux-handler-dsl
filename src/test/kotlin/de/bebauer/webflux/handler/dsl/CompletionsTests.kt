package de.bebauer.webflux.handler.dsl

import io.kotlintest.matchers.collections.containExactly
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters.fromObject
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

class CompletionsTests : WordSpec({
    "complete" should {
        "terminate the handler with the specified status and without a body" {
            runHandlerTest(handler {
                complete(HttpStatus.UNAUTHORIZED)
            }, { expectStatus().isUnauthorized.expectBody().isEmpty })
        }

        "terminate the handler with the specified status and with a body from the specified Mono" {
            runHandlerTest(
                handler {
                    complete(HttpStatus.UNAUTHORIZED, Mono.just("xxx")) {
                        header("test", "xxx")
                    }
                },
                {
                    expectStatus().isUnauthorized
                        .expectHeader().value("test") { it shouldBe "xxx" }
                        .expectBody(String::class.java)
                        .returnResult().responseBody shouldBe "xxx"
                })
        }

        "terminate the handler with the specified status and with a body from the specified Flux" {
            runHandlerTest(
                handler {
                    complete(HttpStatus.UNAUTHORIZED, Flux.fromIterable(listOf(1, 2)))
                },
                {
                    expectStatus().isUnauthorized
                        .expectBodyList(Int::class.java)
                        .returnResult().responseBody should containExactly(1, 2)
                })
        }

        "terminate the handler with the specified status and with a body from the specified value" {
            runHandlerTest(
                handler {
                    complete(HttpStatus.UNAUTHORIZED, "xxx")
                },
                {
                    expectStatus().isUnauthorized
                        .expectBody(String::class.java)
                        .returnResult().responseBody shouldBe "xxx"
                })
        }

        "terminate the handler with the specified status and the response from the specified builder" {
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
                        .expectBody(String::class.java)
                        .returnResult().responseBody shouldBe "123"
                })
        }

        "terminate the handler with the specified status and the response from the specified BodyInserter" {
            runHandlerTest(
                handler {
                    complete(HttpStatus.UNAUTHORIZED, fromObject("123"))
                },
                {
                    expectStatus().isUnauthorized
                        .expectBody(String::class.java)
                        .returnResult().responseBody shouldBe "123"
                })
        }

        "terminate the handler with a nested complete operation" {
            runHandlerTest(
                handler {
                    complete(complete(HttpStatus.UNAUTHORIZED, fromObject("123")).toMono())
                },
                {
                    expectStatus().isUnauthorized
                        .expectBody(String::class.java)
                        .returnResult().responseBody shouldBe "123"
                })
        }

        "terminate the handler with a nested complete operation by extension method" {
            runHandlerTest(
                handler {
                    "123".toMono().map {
                        complete(HttpStatus.UNAUTHORIZED, fromObject(it))
                    }.switchIfEmpty(Mono.defer { notFound().toMono() }).toCompleteOperation()
                },
                {
                    expectStatus().isUnauthorized
                        .expectBody(String::class.java)
                        .returnResult().responseBody shouldBe "123"
                })
        }
    }
})