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
       "allow completions inside if - else" {
            forall(row("a"), row("b")) { value ->
                runHandlerTest(
                    handler {
                        if (value == "a") ok("a") else ok("b")
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
                        ok("Test")
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
                    ok(Mono.empty<String>()) or ok(Mono.empty<String>()) or complete(HttpStatus.NOT_FOUND, "Test")
                },
                {
                    expectStatus().isNotFound.expectBody(String::class.java).returnResult()
                        .responseBody shouldBe "Test"
                })
        }

        "complete with the first non empty completion in a chain (first)" {
            runHandlerTest(
                handler {
                    ok("1") or complete(HttpStatus.NOT_FOUND, "2")
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult()
                        .responseBody shouldBe "1"
                })
        }

        "complete with the first non empty completion in a chain (middle)" {
            runHandlerTest(
                handler {
                    ok(Mono.empty<String>()) or ok("123") or complete(HttpStatus.NOT_FOUND, "Test")
                },
                {
                    expectStatus().isOk.expectBody(String::class.java).returnResult()
                        .responseBody shouldBe "123"
                })
        }

        "allow failWith in a completions chain" {
            runHandlerTest(
                handler {
                    ok(Mono.empty<String>()) or failWith("Error!")
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