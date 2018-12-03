package de.bebauer.webflux.handler.dsl

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class RequestTests {

    @Test
    fun `extract request URI`() {
        runHandlerTest(
            handler {
                extractRequestUri { uri ->
                    complete(uri)
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { Assertions.assertThat(responseBody).isEqualTo("\"http://localhost/test\"") }
            }
        )
    }
}