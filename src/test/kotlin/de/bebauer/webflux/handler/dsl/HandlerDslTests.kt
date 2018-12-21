package de.bebauer.webflux.handler.dsl

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.tables.row
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

class HandlerDslTests : WordSpec({

    "handler DSL" should {
        "fail without a complete" {
            runHandlerTest(
                handler { },
                { expectStatus().is5xxServerError })
        }

        "fail with multiple completes" {
            runHandlerTest(handler {
                complete("xxx")
                complete("abc")
            }, { expectStatus().is5xxServerError })
        }

        "allow completions inside if - else" {
            forall(row("a"), row("b")) { value ->
                runHandlerTest(
                    handler {
                        if (value == "a") complete("a") else complete("b")
                    },
                    {
                        expectStatus().isOk
                            .expectBody(String::class.java)
                            .returnResult().responseBody shouldBe value
                    })
            }
        }

        "execute a sub DSL which produces a correct response" {
            runHandlerTest(
                handler {
                    val result = execute {
                        complete("Test")
                    }

                    complete(result.flatMap { ServerResponse.from(it).body(fromObject("Yay!")) })
                },
                {
                    expectStatus().isOk
                        .expectBody(String::class.java)
                        .returnResult().responseBody shouldBe "Yay!"
                })
        }

        "execute a sub DSL which produces an error" {
            runHandlerTest(
                handler {
                    val result = execute {
                        failWith("Test")
                    }

                    complete(result.onErrorResume { ServerResponse.ok().body(fromObject("Nay!")) })
                },
                {
                    expectStatus().isOk
                        .expectBody(String::class.java)
                        .returnResult().responseBody shouldBe "Nay!"
                })
        }

        "complete with the first non empty completion in a chain (last)" {
            runHandlerTest(
                handler {
                    complete(Mono.empty()) or complete(Mono.empty<String>()) or complete(HttpStatus.NOT_FOUND, "Test")
                },
                {
                    expectStatus().isNotFound.expectBody(String::class.java).returnResult()
                        .responseBody shouldBe "Test"
                })
        }

        "complete with the first non empty completion in a chain (first)" {
            runHandlerTest(
                handler {
                    complete("1") or complete(HttpStatus.NOT_FOUND, "1")
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult()
                        .responseBody shouldBe "1"
                })
        }

        "allow failWith in a completions chain" {
            runHandlerTest(
                handler {
                    complete(Mono.empty()) or failWith("Error!")
                },
                {
                    expectStatus().is5xxServerError
                })
        }
    }

    "failWith" should {
        "cause a status code response when called with a 'ResponseStatusException'" {
            runHandlerTest(
                handler { failWith(ResponseStatusException(HttpStatus.BAD_REQUEST)) },
                { expectStatus().isBadRequest })
        }

        "cause an internal server error if called with a 'String'" {
            runHandlerTest(
                handler { failWith("Failure") },
                { expectStatus().is5xxServerError })
        }
    }
})