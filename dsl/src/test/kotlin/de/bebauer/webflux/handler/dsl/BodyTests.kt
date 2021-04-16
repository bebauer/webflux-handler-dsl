package de.bebauer.webflux.handler.dsl

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters.fromValue
import reactor.kotlin.core.publisher.toFlux

class BodyTests : WordSpec() {

    data class Entity(val value: String)

    init {
        "extractRequestBodyToMono" should {
            "extract request body to a Mono" {
                runHandlerTest(
                    handler {
                        extractRequestBodyToMono<Entity> { body ->
                            ok(body)
                        }
                    },
                    {
                        expectStatus().isOk
                            .expectBody(Entity::class.java)
                            .returnResult().responseBody shouldBe Entity("test")
                    },
                    route = { POST("/test", it) },
                    request = { post().uri("/test").body(fromValue(Entity("test"))) })
            }
        }

        "extractRequestBodyToFlux" should {
            "extract request body to a Flux" {
                val entities = listOf(Entity("1"), Entity("1"), Entity("1"))

                runHandlerTest(
                    handler {
                        extractRequestBodyToFlux<Entity> { body ->
                            ok(body)
                        }
                    },
                    {
                        expectStatus().isOk
                            .expectBodyList(Entity::class.java)
                            .returnResult().responseBody shouldBe entities
                    },
                    route = { POST("/test", it) },
                    request = { post().uri("/test").body(entities.toFlux(), Entity::class.java) })
            }
        }

        "extractRequestBody" should {
            "extract request body with extractor" {
                runHandlerTest(
                    handler {
                        extractRequestBody(BodyExtractors.toMono(Entity::class.java)) { body ->
                            ok(body)
                        }
                    },
                    {
                        expectStatus().isOk
                            .expectBody(Entity::class.java)
                            .returnResult().responseBody shouldBe Entity("test")
                    },
                    route = { POST("/test", it) },
                    request = { post().uri("/test").body(fromValue(Entity("test"))) })
            }

            "extract request body with extractor and hints" {
                runHandlerTest(
                    handler {
                        extractRequestBody(BodyExtractors.toMono(Entity::class.java), mapOf()) { body ->
                            ok(body)
                        }
                    },
                    {
                        expectStatus().isOk
                            .expectBody(Entity::class.java)
                            .returnResult().responseBody shouldBe Entity("test")
                    },
                    route = { POST("/test", it) },
                    request = { post().uri("/test").body(fromValue(Entity("test"))) })
            }
        }
    }
}