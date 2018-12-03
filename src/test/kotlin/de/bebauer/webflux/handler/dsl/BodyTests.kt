package de.bebauer.webflux.handler.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters.fromObject
import reactor.core.publisher.toFlux

class BodyTests {

    data class Entity(val value: String)

    @Test
    fun `extract request body to a Mono`() {
        runHandlerTest(
            handler {
                extractRequestBodyToMono<Entity> { body ->
                    complete(body)
                }
            },
            {
                expectStatus().isOk.expectBody(Entity::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo(Entity("test")) }
            },
            route = { POST("/test", it) },
            request = { post().uri("/test").body(fromObject(Entity("test"))) })
    }

    @Test
    fun `extract request body to a Flux`() {
        val entities = listOf(Entity("1"), Entity("1"), Entity("1"))

        runHandlerTest(
            handler {
                extractRequestBodyToFlux<Entity> { body ->
                    complete(body)
                }
            },
            {
                expectStatus().isOk.expectBodyList(Entity::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo(entities) }
            },
            route = { POST("/test", it) },
            request = { post().uri("/test").body(entities.toFlux(), Entity::class.java) })
    }

    @Test
    fun `extract request body with extractor`() {
        runHandlerTest(
            handler {
                extractRequestBody(BodyExtractors.toMono(Entity::class.java)) { body ->
                    complete(body)
                }
            },
            {
                expectStatus().isOk.expectBody(Entity::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo(Entity("test")) }
            },
            route = { POST("/test", it) },
            request = { post().uri("/test").body(fromObject(Entity("test"))) })
    }

    @Test
    fun `extract request body with extractor and hints`() {
        runHandlerTest(
            handler {
                extractRequestBody(BodyExtractors.toMono(Entity::class.java), mapOf()) { body ->
                    complete(body)
                }
            },
            {
                expectStatus().isOk.expectBody(Entity::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo(Entity("test")) }
            },
            route = { POST("/test", it) },
            request = { post().uri("/test").body(fromObject(Entity("test"))) })
    }
}