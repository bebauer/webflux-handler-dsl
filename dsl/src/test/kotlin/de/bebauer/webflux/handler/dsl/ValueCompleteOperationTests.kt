package de.bebauer.webflux.handler.dsl

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import reactor.core.publisher.toMono

class ValueCompleteOperationTests : WordSpec({
    "ValueCompleteOperationTests" should {
        "map the body" {
            runHandlerTest(
                handler {
                    ok("123").map { it.toInt() }
                },
                {
                    expectStatus().isOk
                        .expectBody(Int::class.java)
                        .returnResult().responseBody shouldBe 123
                })
        }

        "flatMap the body" {
            runHandlerTest(
                handler {
                    ok("123").flatMap { _, _, _ ->
                        notFound()
                    }
                },
                {
                    expectStatus().isNotFound
                        .expectBody().isEmpty
                })
        }

        "flatMapMono the body" {
            runHandlerTest(
                handler {
                    ok("123").flatMapMono { _, _, _ ->
                        notFound().toMono()
                    }
                },
                {
                    expectStatus().isNotFound
                        .expectBody().isEmpty
                })
        }
    }
})