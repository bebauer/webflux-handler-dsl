package de.bebauer.webflux.handler.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CookiesTests {

    @Test
    fun `required cookie set`() {
        runHandlerTest(
            handler {
                cookie("test".stringCookie) { (test) ->
                    complete(test)
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("xxx") }
            },
            request = { get().uri("/test").cookie("test", "xxx") })
    }

    @Test
    fun `required cookie missing`() {
        runHandlerTest(
            handler {
                cookie("test".stringCookie) { (test) ->
                    complete(test)
                }
            },
            { expectStatus().isBadRequest })
    }

    @Test
    fun `optional cookie set`() {
        runHandlerTest(
            handler {
                cookie("test".stringCookie.optional) { test ->
                    complete(test.toString())
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("Some([xxx])") }
            },
            request = { get().uri("/test").cookie("test", "xxx") })
    }

    @Test
    fun `optional cookie missing`() {
        runHandlerTest(
            handler {
                cookie("test".stringCookie.optional) { test ->
                    complete(test.toString())
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("None") }
            })
    }

    @Test
    fun `optional cookie with default set`() {
        runHandlerTest(
            handler {
                cookie("test".stringCookie.optional(listOf("abc"))) { (test) ->
                    complete(test)
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("xxx") }
            },
            request = { get().uri("/test").cookie("test", "xxx") })
    }

    @Test
    fun `optional cookie with default missing`() {
        runHandlerTest(
            handler {
                cookie("test".stringCookie.optional(listOf("abc"))) { (test) ->
                    complete(test)
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("abc") }
            })
    }

    @Test
    fun `single cookie set`() {
        runHandlerTest(
            handler {
                cookie("test".stringCookie.single) { test ->
                    complete(test)
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("xxx") }
            },
            request = { get().uri("/test").cookie("test", "xxx") })
    }

    @Test
    fun `single cookie missing`() {
        runHandlerTest(
            handler {
                cookie("test".stringCookie.single) { test ->
                    complete(test)
                }
            },
            {
                expectStatus().isBadRequest
            })
    }

    @Test
    fun `single optional cookie set`() {
        runHandlerTest(
            handler {
                cookie("test".stringCookie.single.optional) { test ->
                    complete(test.toString())
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("Some(xxx)") }
            },
            request = { get().uri("/test").cookie("test", "xxx") })
    }

    @Test
    fun `single optional cookie missing`() {
        runHandlerTest(
            handler {
                cookie("test".stringCookie.single.optional) { test ->
                    complete(test.toString())
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("None") }
            })
    }

    @Test
    fun `single optional cookie with default set`() {
        runHandlerTest(
            handler {
                cookie("test".stringCookie.single.optional("abc")) { test ->
                    complete(test)
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("xxx") }
            },
            request = { get().uri("/test").cookie("test", "xxx") })
    }

    @Test
    fun `single optional cookie with default missing`() {
        runHandlerTest(
            handler {
                cookie("test".stringCookie.single.optional("abc")) { test ->
                    complete(test)
                }
            },
            {
                expectStatus().isOk.expectBody(String::class.java).returnResult()
                    .apply { assertThat(responseBody).isEqualTo("abc") }
            })
    }
}