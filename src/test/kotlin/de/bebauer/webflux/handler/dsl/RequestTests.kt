package de.bebauer.webflux.handler.dsl

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

class RequestTests : WordSpec({

    "extractRequestUri" should {
        "extract the request URI from the 'ServerRequest'" {
            runHandlerTest(
                handler {
                    extractRequestUri { uri ->
                        complete(uri.toString())
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
                        complete(host)
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
                        complete(scheme)
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