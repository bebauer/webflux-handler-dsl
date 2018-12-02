package de.bebauer.webflux.handler.dsl

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.router

class CookiesTests {

    @Test
    fun `required cookie set`() {
        val router = router {
            GET("/test", handler {
                cookie("test") { (test) ->
                    complete {
                        ok().body(BodyInserters.fromObject(test))
                    }
                }
            })
        }

        WebTestClient.bindToRouterFunction(router)
            .configureClient()
            .baseUrl("http://localhost")
            .build()
            .get()
            .uri("/test")
            .cookie("test", "xyz")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult().apply { Assertions.assertThat(responseBody).isEqualTo("xyz") }
    }

    @Test
    fun `required cookie missing`() {
        val router = router {
            GET("/test", handler {
                cookie("test") { (test) ->
                    complete {
                        ok().body(BodyInserters.fromObject(test))
                    }
                }
            })
        }

        WebTestClient.bindToRouterFunction(router)
            .configureClient()
            .baseUrl("http://localhost")
            .build()
            .get()
            .uri("/test")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `optional cookie set`() {
        val router = router {
            GET("/test", handler {
                optionalCookie("test") { test ->
                    complete {
                        ok().body(BodyInserters.fromObject(test.toString()))
                    }
                }
            })
        }

        WebTestClient.bindToRouterFunction(router)
            .configureClient()
            .baseUrl("http://localhost")
            .build()
            .get()
            .uri("/test")
            .cookie("test", "xyz")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .apply { Assertions.assertThat(responseBody).isEqualTo("Some([xyz])") }
    }

    @Test
    fun `optional cookie missing`() {
        val router = router {
            GET("/test", handler {
                optionalCookie("test") { test ->
                    complete {
                        ok().body(BodyInserters.fromObject(test.toString()))
                    }
                }
            })
        }

        WebTestClient.bindToRouterFunction(router)
            .configureClient()
            .baseUrl("http://localhost")
            .build()
            .get()
            .uri("/test")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult().apply { Assertions.assertThat(responseBody).isEqualTo("None") }
    }
}