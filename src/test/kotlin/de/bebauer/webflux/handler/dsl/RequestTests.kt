package de.bebauer.webflux.handler.dsl

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class RequestTests {

    @Test
    fun `extract request URI`() {
        runHandlerTest(
            handler {
                extractRequestUri { uri ->
                    complete(uri.toString())
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { Assertions.assertThat(responseBody).isEqualTo("http://localhost/test") }
            }
        )
    }

    @Test
    fun `extract request host`() {
        runHandlerTest(
            handler {
                extractHost { host ->
                    complete(host)
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { Assertions.assertThat(responseBody).isEqualTo("localhost") }
            }
        )
    }

    @Test
    fun `extract request scheme`() {
        runHandlerTest(
            handler {
                extractScheme { scheme ->
                    complete(scheme)
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { Assertions.assertThat(responseBody).isEqualTo("http") }
            }
        )
    }
}