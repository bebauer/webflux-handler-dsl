package de.bebauer.webflux.handler.dsl

import arrow.core.None
import arrow.core.Some
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.router
import reactor.kotlin.core.publisher.toFlux
import java.math.BigDecimal
import java.math.BigInteger

@ExperimentalUnsignedTypes
class QueryParametersTests : WordSpec() {

    enum class TestEnum {
        @Suppress("Unused")
        FIRST,

        @Suppress("EnumEntryName")
        second,
        Third
    }

    private val client: WebTestClient by lazy {
        val router = router {
            GET("/blah", handler {
                parameters(
                    "v1".stringParam,
                    "v2".stringParam.optional,
                    "v3".intParam,
                    "v4".intParam.optional,
                    "v5".stringParam.optional("default"),
                    "v6".intParam.repeated,
                    "v7".intParam.repeated.optional,
                    "v8".intParam.repeated.optional(
                        listOf(9, 8, 7)
                    ),
                    "v9".intParam.nullable,
                    "v10".intParam.repeated.nullable
                ) { v1, v2, v3, v4, v5, v6, v7, v8, v9, v10 ->
                    ok {
                        body(fromValue("$v1 - $v2 - $v3 - $v4 - $v5 - $v6 - $v7 - $v8 - $v9 - $v10"))
                    }
                }
            })
        }

        WebTestClient.bindToRouterFunction(router)
            .configureClient()
            .baseUrl("http://localhost")
            .build()
    }

    init {
        "parameters" should {
            "extract all parameters when all are set" {
                client.get()
                    .uri("/blah?v1=a&v2=b&v3=3&v4=4&v5=c&v6=1&v6=2&v7=3&v7=4&v8=5&v8=6&v9=7&v10=10&v10=11")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody(String::class.java)
                    .returnResult()
                    .responseBody shouldBe "a - Option.Some(b) - 3 - Option.Some(4) - c - [1, 2] - Option.Some([3, 4]) - [5, 6] - 7 - [10, 11]"
            }

            "extract the required parameters and those with default values if optional parameters are missing" {
                client.get()
                    .uri("/blah?v1=a&v3=3&v6=1&v6=2")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody(String::class.java)
                    .returnResult()
                    .responseBody shouldBe "a - Option.None - 3 - Option.None - default - [1, 2] - Option.None - [9, 8, 7] - null - null"
            }

            "fail with bad request if required parameters are missing" {
                client.get()
                    .uri("/blah")
                    .exchange()
                    .expectStatus().isBadRequest
            }

            "extract CSV string list parameters" {
                val router = router {
                    GET("/blah", handler {
                        parameter("test".csvParam) { test ->
                            ok {
                                contentType(MediaType.APPLICATION_JSON).body(fromValue(test))
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
                    .returnResult().responseBody shouldBe listOf("x", "y", "z")
            }

            "extract CSV int list parameters" {
                val router = router {
                    GET("/blah", handler {
                        parameter("test".csvParam(String::toInt)) { test ->
                            ok {
                                contentType(MediaType.APPLICATION_JSON).body(test.toFlux(), Int::class.java)
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
                    .expectBodyList(Int::class.java)
                    .returnResult().responseBody should containExactly(1, 2, 3)
            }
        }

        "parameter" should {
            "extract a string query parameter" {
                forAll(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::stringParam, testMode, "abc")
                }
            }

            "extract a int query parameter" {
                forAll(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::intParam, testMode, Int.MAX_VALUE)
                }
            }

            "extract a short query parameter" {
                forAll(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::shortParam, testMode, Short.MAX_VALUE)
                }
            }

            "extract a long query parameter" {
                forAll(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::longParam, testMode, Long.MAX_VALUE)
                }
            }

            "extract a byte query parameter" {
                forAll(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::byteParam, testMode, Byte.MAX_VALUE)
                }
            }

            "extract a double query parameter" {
                forAll(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::doubleParam, testMode, Double.MAX_VALUE)
                }
            }

            "extract a float query parameter" {
                forAll(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::floatParam, testMode, Float.MAX_VALUE)
                }
            }

            "extract a BigDecimal query parameter" {
                forAll(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(
                        String::bigDecimalParam,
                        testMode,
                        BigDecimal.valueOf(Double.MAX_VALUE).add(1.toBigDecimal())
                    )
                }
            }

            "extract a BigInteger query parameter" {
                forAll(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(
                        String::bigIntegerParam,
                        testMode,
                        BigInteger.valueOf(Long.MAX_VALUE).add(1.toBigInteger())
                    )
                }
            }

            "extract a boolean query parameter" {
                forAll(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::booleanParam, testMode, true)
                }
            }

            "extract a uInt query parameter" {
                forAll(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::uIntParam, testMode, UInt.MAX_VALUE)
                }
            }

            "extract a uLong query parameter" {
                forAll(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::uLongParam, testMode, ULong.MAX_VALUE)
                }
            }

            "extract a uByte query parameter" {
                forAll(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::uByteParam, testMode, UByte.MAX_VALUE)
                }
            }

            "extract a uShort query parameter" {
                forAll(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::uShortParam, testMode, UShort.MAX_VALUE)
                }
            }

            "extract a enum query parameter" {
                forAll(*TestMode.rows) { testMode ->
                    testTypedQueryParameter({ this.enumParam() }, testMode, TestEnum.Third)
                }
            }

            "extract uppercase string enum" {
                runHandlerTest(
                    handler {
                        parameter("test".stringParam.toUpperCase.toEnum<TestEnum>()) { test ->
                            ok(test.toString())
                        }
                    },
                    {
                        expectStatus().isOk
                            .expectBody(String::class.java)
                            .returnResult()
                            .responseBody shouldBe "FIRST"
                    },
                    { GET("/test", it) },
                    { get().uri("/test?test=fiRst") })
            }

            "extract lowercase string enum" {
                runHandlerTest(
                    handler {
                        parameter("test".stringParam.toLowerCase.toEnum<TestEnum>()) { test ->
                            ok(test.toString())
                        }
                    },
                    {
                        expectStatus().isOk
                            .expectBody(String::class.java)
                            .returnResult()
                            .responseBody shouldBe "second"
                    },
                    { GET("/test", it) },
                    { get().uri("/test?test=SEcOnD") })
            }

            "extract exact string enum" {
                runHandlerTest(
                    handler {
                        parameter("test".stringParam.toEnum<TestEnum>()) { test ->
                            ok(test.toString())
                        }
                    },
                    {
                        expectStatus().isOk
                            .expectBody(String::class.java)
                            .returnResult()
                            .responseBody shouldBe "Third"
                    },
                    { GET("/test", it) },
                    { get().uri("/test?test=Third") })
            }

            "fail extracting string enum" {
                runHandlerTest(
                    handler {
                        parameter("test".stringParam.toEnum<TestEnum>()) { test ->
                            ok(test.toString())
                        }
                    },
                    {
                        expectStatus().isBadRequest
                    },
                    { GET("/test", it) },
                    { get().uri("/test?test=first") })
            }
        }
    }

    enum class TestMode {
        DEFAULT,
        REPEATED,
        OPTIONAL_SET,
        OPTIONAL_MISSING,
        OPTIONAL_MISSING_DEFAULT,
        OPTIONAL_REPEATED_SET,
        OPTIONAL_REPEATED_MISSING,
        OPTIONAL_REPEATED_MISSING_DEFAULT,
        NULLABLE_SET,
        NULLABLE_MISSING,
        NULLABLE_REPEATED_SET,
        NULLABLE_REPEATED_MISSING;

        companion object {
            val rows = values().map { row(it) }.toTypedArray()
        }
    }

    private fun <T> testTypedQueryParameter(
        parameter: String.() -> QueryParameter<T, T>,
        testMode: TestMode,
        value: T
    ) {
        val testConfig = when (testMode) {
            TestMode.DEFAULT -> "test".parameter() to value
            TestMode.REPEATED -> "test".parameter().repeated to listOf(value)
            TestMode.OPTIONAL_SET -> "test".parameter().optional to Some(value)
            TestMode.OPTIONAL_MISSING -> "test".parameter().optional to None
            TestMode.OPTIONAL_MISSING_DEFAULT -> "test".parameter().optional(value) to value
            TestMode.OPTIONAL_REPEATED_SET -> "test".parameter().repeated.optional to Some(listOf(value))
            TestMode.OPTIONAL_REPEATED_MISSING -> "test".parameter().repeated.optional to None
            TestMode.OPTIONAL_REPEATED_MISSING_DEFAULT -> "test".parameter().repeated.optional(listOf(value)) to listOf(
                value
            )
            TestMode.NULLABLE_SET -> "test".parameter().nullable to value
            TestMode.NULLABLE_MISSING -> "test".parameter().nullable to null
            TestMode.NULLABLE_REPEATED_SET -> "test".parameter().repeated.nullable to listOf(value)
            TestMode.NULLABLE_REPEATED_MISSING -> "test".parameter().repeated.nullable to null
        }

        runHandlerTest(
            handler {
                parameter(testConfig.first) { test ->
                    ok(test.toString())
                }
            },
            {
                expectStatus().isOk
                    .expectBody(String::class.java)
                    .returnResult()
                    .responseBody shouldBe testConfig.second.toString()
            },
            { GET("/test", it) },
            { get().uri("/test" + if (testConfig.second is None || testConfig.second == null) "" else "?test=${value.toString()}") })
    }
}