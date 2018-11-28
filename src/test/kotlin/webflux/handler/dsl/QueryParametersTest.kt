package webflux.handler.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.router

class QueryParametersTest {

    private val client: WebTestClient by lazy {
        val router = router {
            GET("/blah", handler {
                parameters(
                    "v1".stringParam(),
                    "v2".stringParam().optional(),
                    "v3".intParam(),
                    "v4".intParam().optional(),
                    "v5".stringParam().optional("default"),
                    "v6".intParam().repeated(),
                    "v7".intParam().repeated().optional(),
                    "v8".intParam().repeated().optional(
                        listOf(9, 8, 7)
                    )
                ) { v1, v2, v3, v4, v5, v6, v7, v8 ->
                    respond {
                        ok().body(fromObject("$v1 - $v2 - $v3 - $v4 - $v5 - $v6 - $v7 - $v8"))
                    }
                }
            })
        }

        WebTestClient.bindToRouterFunction(router)
            .configureClient()
            .baseUrl("http://localhost")
            .build()
    }

    @Test
    fun `all parameters set`() {
        client.get()
            .uri("/blah?v1=a&v2=b&v3=3&v4=4&v5=c&v6=1&v6=2&v7=3&v7=4&v8=5&v8=6")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult().apply { assertThat(responseBody).isEqualTo("a - b - 3 - 4 - c - [1, 2] - [3, 4] - [5, 6]") }
    }

    @Test
    fun `only required parameters set`() {
        client.get()
            .uri("/blah?v1=a&v3=3&v6=1&v6=2")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .apply { assertThat(responseBody).isEqualTo("a - null - 3 - null - default - [1, 2] - null - [9, 8, 7]") }
    }

    @Test
    fun `required parameters missing`() {
        client.get()
            .uri("/blah")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `csvParam should handle string lists`() {
        val router = router {
            GET("/blah", handler {
                parameter("test".csvParam()) { test ->
                    respond {
                        ok().contentType(MediaType.APPLICATION_JSON).body(fromObject(test))
                    }
                }
            })
        }

        WebTestClient.bindToRouterFunction(router)
            .configureClient()
            .baseUrl("http://localhost")
            .build()
            .get()
            .uri("/blah?test=x,y,z")
            .exchange()
            .expectStatus().isOk
            .expectBody(List::class.java)
            .returnResult().apply { assertThat(responseBody).containsExactly("x", "y", "z") }
    }

    @Test
    fun `csvParam should handle int lists`() {
        val router = router {
            GET("/blah", handler {
                parameter("test".csvParam(String::toInt)) { test ->
                    respond {
                        ok().contentType(MediaType.APPLICATION_JSON).body(fromObject(test))
                    }
                }
            })
        }

        WebTestClient.bindToRouterFunction(router)
            .configureClient()
            .baseUrl("http://localhost")
            .build()
            .get()
            .uri("/blah?test=1,2,3")
            .exchange()
            .expectStatus().isOk
            .expectBody(List::class.java)
            .returnResult().apply { assertThat(responseBody).containsExactly(1, 2, 3) }
    }
}