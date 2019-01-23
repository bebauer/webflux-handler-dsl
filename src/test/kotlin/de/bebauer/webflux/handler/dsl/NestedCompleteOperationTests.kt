package de.bebauer.webflux.handler.dsl

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import reactor.core.publisher.toMono

class NestedCompleteOperationTests : WordSpec({
    "NestedCompleteOperation" should {
        "flatMap the operation" {
            runHandlerTest(
                handler {
                    ok("123").toMono().toCompleteOperation().flatMap { op ->
                        op.map { it.map(String::toInt) }
                    }
                },
                {
                    expectStatus().isOk
                        .expectBody(Int::class.java)
                        .returnResult().apply {
                            responseBody shouldBe 123
                        }
                })
        }
    }
})