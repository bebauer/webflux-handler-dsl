package de.bebauer.webflux.handler.dsl

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters.fromObject
import reactor.core.publisher.toFlux

class BodyTests : WordSpec() {

    data class Entity(val value: String)

    init {
        "extractRequestBodyToMono" should {
            "extract request body to a Mono" {
                runHandlerTest(
                    handler {
                        extractRequestBodyToMono<Entity> { body ->
                            complete(body)
                        }
                    },
                    {
                        expectStatus().isOk.expectBody(Entity::class.java).returnResult()
                            .apply { responseBody shouldBe Entity("test") }
                    },
                    route = { POST("/test", it) },
                    request = { post().uri("/test").body(fromObject(Entity("test"))) })
            }
        }

        "extractRequestBodyToFlux" should {
            "extract request body to a Flux" {
                val entities = listOf(Entity("1"), Entity("1"), Entity("1"))

                runHandlerTest(
                    handler {
                        extractRequestBodyToFlux<Entity> { body ->
                            complete(body)
                        }
                    },
                    {
                        expectStatus().isOk.expectBodyList(Entity::class.java).returnResult()
                            .apply { responseBody shouldBe entities }
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
                            complete(body)
                        }
                    },
                    {
                        expectStatus().isOk.expectBody(Entity::class.java).returnResult()
                            .apply { responseBody shouldBe Entity("test") }
                    },
                    route = { POST("/test", it) },
                    request = { post().uri("/test").body(fromObject(Entity("test"))) })
            }

            "extract request body with extractor and hints" {
                runHandlerTest(
                    handler {
                        extractRequestBody(BodyExtractors.toMono(Entity::class.java), mapOf()) { body ->
                            complete(body)
                        }
                    },
                    {
                        expectStatus().isOk.expectBody(Entity::class.java).returnResult()
                            .apply { responseBody shouldBe Entity("test") }
                    },
                    route = { POST("/test", it) },
                    request = { post().uri("/test").body(fromObject(Entity("test"))) })
            }
        }
    }
}