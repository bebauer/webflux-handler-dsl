package de.bebauer.webflux.handler.dsl

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

class MonoBodyCompleteOperationTests : WordSpec({
    "MonoBodyCompleteOperation" should {
        "map the body" {
            runHandlerTest(
                handler {
                    ok(Mono.just("123")).map { it.toInt() }
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
                    ok(Mono.just("123")).flatMap { _, _, _ ->
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
                    ok(Mono.just("123")).flatMapMono { _, _, _ ->
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