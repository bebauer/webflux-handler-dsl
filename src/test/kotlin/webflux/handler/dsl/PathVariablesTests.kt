package webflux.handler.dsl

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.router

class PathVariablesTests {

    @Test
    fun `string path variable`() {
        val router = router {
            GET("/test/{test}", handler {
                pathVariable("test".stringVar()) { test ->
                    complete {
                        ok().contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromObject(test))
                    }
                }
            })
        }

        WebTestClient.bindToRouterFunction(router)
                .configureClient()
                .baseUrl("http://localhost")
                .build()
                .get()
                .uri("/test/abc")
                .exchange()
                .expectStatus().isOk
                .expectBody(String::class.java)
                .returnResult().apply { Assertions.assertThat(responseBody).isEqualTo("abc") }
    }

    @Test
    fun `int path variable`() {
        val router = router {
            GET("/test/{test}", handler {
                pathVariable("test".intVar()) { test ->
                    complete {
                        ok().contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromObject(test))
                    }
                }
            })
        }

        WebTestClient.bindToRouterFunction(router)
                .configureClient()
                .baseUrl("http://localhost")
                .build()
                .get()
                .uri("/test/123")
                .exchange()
                .expectStatus().isOk
                .expectBody(Int::class.java)
                .returnResult().apply { Assertions.assertThat(responseBody).isEqualTo(123) }
    }

    @Test
    fun `multiple mixed path variables`() {
        val router = router {
            GET("/test/{test1}/{test2}/{test3}", handler {
                pathVariables("test1".intVar(),
                              "test2".stringVar(),
                              "test3".stringVar()) { test1, test2, test3 ->
                    complete {
                        ok().contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromObject(listOf(test1, test2, test3)))
                    }
                }
            })
        }

        WebTestClient.bindToRouterFunction(router)
                .configureClient()
                .baseUrl("http://localhost")
                .build()
                .get()
                .uri("/test/123/a/b")
                .exchange()
                .expectStatus().isOk
                .expectBody(List::class.java)
                .returnResult()
                .apply { Assertions.assertThat(responseBody).containsExactly(123, "a", "b") }
    }
}