package webflux.handler.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

class CompletionsTests {

    @Test
    fun `ok without body`() {
        runTest(handler {
            complete()
        }, { it.expectStatus().isOk.expectBody().isEmpty })
    }

    @Test
    fun `status without body`() {
        runTest(handler {
            complete(HttpStatus.UNAUTHORIZED)
        }, { it.expectStatus().isUnauthorized.expectBody().isEmpty })
    }

    @Test
    fun `complete with publisher`() {
        runTest(
                handler {
                    complete(Mono.just("xxx"))
                },
                {
                    it.expectStatus().isOk.expectBody(String::class.java).returnResult()
                            .apply { assertThat(responseBody).isEqualTo("xxx") }
                })
    }

    @Test
    fun `complete with value`() {
        runTest(
                handler {
                    complete("xxx")
                },
                {
                    it.expectStatus().isOk.expectBody(String::class.java).returnResult()
                            .apply { assertThat(responseBody).isEqualTo("xxx") }
                })
    }

    @Test
    fun `complete with status and publisher`() {
        runTest(
                handler {
                    complete(HttpStatus.UNAUTHORIZED, Mono.just("xxx"))
                },
                {
                    it.expectStatus().isUnauthorized.expectBody(String::class.java).returnResult()
                            .apply { assertThat(responseBody).isEqualTo("xxx") }
                })
    }

    @Test
    fun `complete with status and value`() {
        runTest(
                handler {
                    complete(HttpStatus.UNAUTHORIZED, "xxx")
                },
                {
                    it.expectStatus().isUnauthorized.expectBody(String::class.java).returnResult()
                            .apply { assertThat(responseBody).isEqualTo("xxx") }
                })
    }

    @Test
    fun `complete with builder`() {
        runTest(
                handler {
                    complete {
                        contentType(MediaType.APPLICATION_JSON)
                        header("xxx", "abc")
                        body(fromObject("123"))
                    }
                },
                {
                    it.expectStatus().isOk
                            .expectHeader().contentType(MediaType.APPLICATION_JSON)
                            .expectHeader().valueEquals("xxx", "abc")
                            .expectBody(String::class.java).returnResult()
                            .apply { assertThat(responseBody).isEqualTo("123") }
                })
    }

    @Test
    fun `complete with status and builder`() {
        runTest(
                handler {
                    complete(HttpStatus.UNAUTHORIZED) {
                        contentType(MediaType.APPLICATION_JSON)
                        header("xxx", "abc")
                        body(fromObject("123"))
                    }
                },
                {
                    it.expectStatus().isUnauthorized
                            .expectHeader().contentType(MediaType.APPLICATION_JSON)
                            .expectHeader().valueEquals("xxx", "abc")
                            .expectBody(String::class.java).returnResult()
                            .apply { assertThat(responseBody).isEqualTo("123") }
                })
    }

    @Test
    fun `complete with body inserter`() {
        runTest(
                handler {
                    complete(fromObject("123"))
                },
                {
                    it.expectStatus().isOk
                            .expectBody(String::class.java).returnResult()
                            .apply { assertThat(responseBody).isEqualTo("123") }
                })
    }

    @Test
    fun `complete with status and body inserter`() {
        runTest(
                handler {
                    complete(HttpStatus.UNAUTHORIZED, fromObject("123"))
                },
                {
                    it.expectStatus().isUnauthorized
                            .expectBody(String::class.java).returnResult()
                            .apply { assertThat(responseBody).isEqualTo("123") }
                })
    }

    private fun runTest(handler: (ServerRequest) -> Mono<out ServerResponse>,
                        validation: (WebTestClient.ResponseSpec) -> Unit) {
        val router = router {
            GET("/test", handler)
        }

        validation(WebTestClient.bindToRouterFunction(router)
                           .configureClient()
                           .baseUrl("http://localhost")
                           .build()
                           .get()
                           .uri("/test")
                           .cookie("test", "xyz")
                           .exchange())
    }
}