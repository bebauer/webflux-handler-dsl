package de.bebauer.webflux.handler.dsl

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class RequestTests : WordSpec({

    "extractRequestUri" should {
        "extract the request URI from the 'ServerRequest'" {
            runHandlerTest(
                handler {
                    extractRequestUri { uri ->
                        ok(uri.toString())
                    }
                },
                {
                    expectStatus().isOk
                        .expectBody(String::class.java)
                        .returnResult().responseBody shouldBe "http://localhost/test"
                }
            )
        }
    }

    "extractHost" should {
        "extract the request host from the 'ServerRequest'" {
            runHandlerTest(
                handler {
                    extractHost { host ->
                        ok(host)
                    }
                },
                {
                    expectStatus().isOk
                        .expectBody(String::class.java)
                        .returnResult().responseBody shouldBe "localhost"
                }
            )
        }
    }

    "extractScheme" should {
        "extract request scheme from the 'ServerRequest'" {
            runHandlerTest(
                handler {
                    extractScheme { scheme ->
                        ok(scheme)
                    }
                },
                {
                    expectStatus().isOk
                        .expectBody(String::class.java)
                        .returnResult().responseBody shouldBe "http"
                }
            )
        }
    }
})