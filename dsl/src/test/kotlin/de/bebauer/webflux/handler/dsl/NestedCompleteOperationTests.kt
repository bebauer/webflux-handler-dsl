package de.bebauer.webflux.handler.dsl

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import reactor.kotlin.core.publisher.toMono


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