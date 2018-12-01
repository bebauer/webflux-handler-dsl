package webflux.handler.dsl

import arrow.core.None
import arrow.core.Some
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.router
import java.math.BigDecimal
import java.math.BigInteger

@ExperimentalUnsignedTypes
class QueryParametersTests {

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
                    complete {
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
            .returnResult()
            .apply { assertThat(responseBody).isEqualTo("a - Some(b) - 3 - Some(4) - Some(c) - [1, 2] - Some([3, 4]) - Some([5, 6])") }
    }

    @Test
    fun `only required parameters set`() {
        client.get()
            .uri("/blah?v1=a&v3=3&v6=1&v6=2")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .apply { assertThat(responseBody).isEqualTo("a - None - 3 - None - Some(default) - [1, 2] - None - Some([9, 8, 7])") }
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
                    complete {
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
                    complete {
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

    @ParameterizedTest
    @EnumSource(TestMode::class)
    fun `string query parameter`(testMode: TestMode) {
        testTypedQueryParameter(String::stringParam, testMode, "abc")
    }

    @ParameterizedTest
    @EnumSource(TestMode::class)
    fun `int query parameter`(testMode: TestMode) {
        testTypedQueryParameter(String::intParam, testMode, Int.MAX_VALUE)
    }

    @ParameterizedTest
    @EnumSource(TestMode::class)
    fun `short query parameter`(testMode: TestMode) {
        testTypedQueryParameter(String::shortParam, testMode, Short.MAX_VALUE)
    }

    @ParameterizedTest
    @EnumSource(TestMode::class)
    fun `long query parameter`(testMode: TestMode) {
        testTypedQueryParameter(String::longParam, testMode, Long.MAX_VALUE)
    }

    @ParameterizedTest
    @EnumSource(TestMode::class)
    fun `byte query parameter`(testMode: TestMode) {
        testTypedQueryParameter(String::byteParam, testMode, Byte.MAX_VALUE)
    }

    @ParameterizedTest
    @EnumSource(TestMode::class)
    fun `double query parameter`(testMode: TestMode) {
        testTypedQueryParameter(String::doubleParam, testMode, Double.MAX_VALUE)
    }

    @ParameterizedTest
    @EnumSource(TestMode::class)
    fun `float query parameter`(testMode: TestMode) {
        testTypedQueryParameter(String::floatParam, testMode, Float.MAX_VALUE)
    }

    @ParameterizedTest
    @EnumSource(TestMode::class)
    fun `BigDecimal query parameter`(testMode: TestMode) {
        testTypedQueryParameter(
            String::bigDecimalParam,
            testMode,
            BigDecimal.valueOf(Double.MAX_VALUE).add(1.toBigDecimal())
        )
    }

    @ParameterizedTest
    @EnumSource(TestMode::class)
    fun `BigInteger query parameter`(testMode: TestMode) {
        testTypedQueryParameter(
            String::bigIntegerParam,
            testMode,
            BigInteger.valueOf(Long.MAX_VALUE).add(1.toBigInteger())
        )
    }

    @ParameterizedTest
    @EnumSource(TestMode::class)
    fun `boolean query parameter`(testMode: TestMode) {
        testTypedQueryParameter(String::booleanParam, testMode, true)
    }

    @ParameterizedTest
    @EnumSource(TestMode::class)
    fun `uInt query parameter`(testMode: TestMode) {
        testTypedQueryParameter(String::uIntParam, testMode, UInt.MAX_VALUE)
    }

    @ParameterizedTest
    @EnumSource(TestMode::class)
    fun `uLong query parameter`(testMode: TestMode) {
        testTypedQueryParameter(String::uLongParam, testMode, ULong.MAX_VALUE)
    }

    @ParameterizedTest
    @EnumSource(TestMode::class)
    fun `uByte query parameter`(testMode: TestMode) {
        testTypedQueryParameter(String::uByteParam, testMode, UByte.MAX_VALUE)
    }

    @ParameterizedTest
    @EnumSource(TestMode::class)
    fun `uShort query parameter`(testMode: TestMode) {
        testTypedQueryParameter(String::uShortParam, testMode, UShort.MAX_VALUE)
    }

    enum class TestMode {
        DEFAULT,
        REPEATED,
        OPTIONAL_SET,
        OPTIONAL_MISSING,
        OPTIONAL_MISSING_DEFAULT,
        OPTIONAL_REPEATED_SET,
        OPTIONAL_REPEATED_MISSING,
        OPTIONAL_REPEATED_MISSING_DEFAULT
    }

    private fun <T> testTypedQueryParameter(
        parameter: String.() -> QueryParameter<T, T>,
        testMode: TestMode,
        value: T
    ) {
        val testConfig = when (testMode) {
            TestMode.DEFAULT -> "test".parameter() to value
            TestMode.REPEATED -> "test".parameter().repeated() to listOf(value)
            TestMode.OPTIONAL_SET -> "test".parameter().optional() to Some(value)
            TestMode.OPTIONAL_MISSING -> "test".parameter().optional() to None
            TestMode.OPTIONAL_MISSING_DEFAULT -> "test".parameter().optional(value) to Some(value)
            TestMode.OPTIONAL_REPEATED_SET -> "test".parameter().repeated().optional() to Some(listOf(value))
            TestMode.OPTIONAL_REPEATED_MISSING -> "test".parameter().repeated().optional() to None
            TestMode.OPTIONAL_REPEATED_MISSING_DEFAULT -> "test".parameter().repeated().optional(listOf(value)) to Some(
                listOf(value)
            )
        }

        runHandlerTest(
            handler {
                parameter(testConfig.first) { test ->
                    complete(test.toString())
                }
            },
            {
                expectStatus().isOk
                    .expectBody(String::class.java)
                    .returnResult()
                    .apply { Assertions.assertThat(responseBody).isEqualTo(testConfig.second.toString()) }
            },
            { GET("/test", it) },
            { get().uri("/test" + if (testConfig.second is None) "" else "?test=${value.toString()}") })
    }
}