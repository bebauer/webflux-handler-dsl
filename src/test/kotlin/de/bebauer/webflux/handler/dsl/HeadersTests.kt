package de.bebauer.webflux.handler.dsl

import arrow.core.None
import arrow.core.toOption
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import java.util.stream.Stream

class HeadersTests {

    data class TestArg<T>(val header: HeaderName<List<T>>, val expected: Any, val value: String) {
        override fun toString(): String {
            return header.name
        }
    }

    companion object {
        private const val TEST_HEADER_VALUE = "XXX"

        private fun <T> testArg(
            header: HeaderName<List<T>>,
            expected: Any = TEST_HEADER_VALUE,
            value: String = TEST_HEADER_VALUE
        ) = arguments(TestArg(header, expected, value))!!

        @Suppress("unused")
        @JvmStatic
        fun headerArguments(): Stream<Arguments> = Stream.of(
            testArg(
                Headers.Accept,
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON_VALUE
            ),
            testArg(
                Headers.ContentType,
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON_VALUE
            ),
            testArg(Headers.Authorization),
            testArg(Headers.Location),
            testArg(Headers.ContentLocation),
            testArg(Headers.AcceptCharset),
            testArg(Headers.AcceptEncoding),
            testArg(Headers.AcceptLanguage),
            testArg(Headers.AcceptRanges),
            testArg(Headers.AccessControlAllowCredentials),
            testArg(Headers.AccessControlAllowHeaders),
            testArg(Headers.AccessControlAllowMethods),
            testArg(Headers.AccessControlAllowOrigin),
            testArg(Headers.AccessControlExposeHeaders),
            testArg(Headers.AccessControlMaxAge),
            testArg(Headers.AccessControlRequestHeaders),
            testArg(Headers.AccessControlRequestMethod),
            testArg(Headers.CacheControl),
            testArg(Headers.Age),
            testArg(Headers.Allow),
            testArg(Headers.Connection),
            testArg(Headers.ContentDisposition),
            testArg(Headers.ContentEncoding),
            testArg(Headers.ContentLanguage),
            testArg(Headers.ContentLength),
            testArg(Headers.ContentRange),
            testArg(Headers.Cookie),
            testArg(Headers.Date),
            testArg(Headers.Etag),
            testArg(Headers.Expect),
            testArg(Headers.Expires),
            testArg(Headers.From),
            testArg(Headers.Host),
            testArg(Headers.IfMatch),
            testArg(Headers.IfModifiedSince),
            testArg(Headers.IfNoneMatch),
            testArg(Headers.IfRange),
            testArg(Headers.IfUnmodifiedSince),
            testArg(Headers.LastModified),
            testArg(Headers.Link),
            testArg(Headers.MaxForwards),
            testArg(Headers.Origin),
            testArg(Headers.Pragma),
            testArg(Headers.ProxyAuthenticate),
            testArg(Headers.ProxyAuthorization),
            testArg(Headers.Upgrade),
            testArg(Headers.Range),
            testArg(Headers.Referer),
            testArg(Headers.RetryAfter),
            testArg(Headers.Server),
            testArg(Headers.SetCookie),
            testArg(Headers.SetCookie2),
            testArg(Headers.Trailer),
            testArg(Headers.TransferEncoding),
            testArg(Headers.UserAgent),
            testArg(Headers.Vary),
            testArg(Headers.Via),
            testArg(Headers.Warning),
            testArg(Headers.TE),
            testArg(Headers.WwwAuthenticate)
        )
    }

    private fun executeClient(
        router: RouterFunction<ServerResponse>,
        header: String,
        vararg values: String
    ) = WebTestClient.bindToRouterFunction(
        router
    )
        .configureClient()
        .baseUrl("http://localhost")
        .build()
        .get()
        .uri("/test")
        .header(header, *values)
        .exchange()
        .expectStatus()

    private fun testHeader(header: HeaderName<*>, expected: String, vararg values: String) {
        val router = router {
            GET("/test", handler {
                headerValue(header) { value ->
                    complete {
                        ok().body(BodyInserters.fromObject(value.toString()))
                    }
                }
            })
        }

        executeClient(router, header.name, *values).isOk
            .expectBody(String::class.java)
            .returnResult().apply { assertThat(responseBody).isEqualTo(expected) }
    }

    @Test
    fun `raw header`() {
        testHeader("test".rawHeader(), "a, b", "a", "b")
    }

    @ParameterizedTest
    @MethodSource("headerArguments")
    fun `provided headers`(arg: TestArg<*>) {
        val (header, expected, value) = arg

        testHeader(header, listOf(expected).toString(), value)
        testHeader(header.single(), expected.toString(), value)
    }

    @Test
    fun `optional header value set`() {
        val router = router {
            GET("/test", handler {
                optionalHeaderValue(Headers.Accept.single()) { accept ->
                    complete {
                        ok().body(BodyInserters.fromObject(accept.toString()))
                    }
                }
            })
        }

        executeClient(router, HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).isOk
            .expectBody(String::class.java)
            .returnResult()
            .apply { assertThat(responseBody).isEqualTo(MediaType.APPLICATION_JSON_VALUE.toOption().toString()) }
    }

    @Test
    fun `optional header value missing`() {
        val router = router {
            GET("/test", handler {
                optionalHeaderValue(Headers.AccessControlMaxAge.single()) { accept ->
                    complete {
                        ok().body(BodyInserters.fromObject(accept.toString()))
                    }
                }
            })
        }

        executeClient(router, HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).isOk
            .expectBody(String::class.java)
            .returnResult()
            .apply { assertThat(responseBody).isEqualTo(None.toString()) }
    }

    @Test
    fun `required header value missing`() {
        val router = router {
            GET("/test", handler {
                headerValue(Headers.AccessControlMaxAge.single()) { accept ->
                    complete {
                        ok().body(BodyInserters.fromObject(accept))
                    }
                }
            })
        }

        executeClient(router, HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).isBadRequest
    }
}