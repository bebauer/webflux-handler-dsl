package de.bebauer.webflux.handler.dsl

import arrow.core.None
import arrow.core.Some
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.containExactly
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.tables.row
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.toFlux
import java.math.BigDecimal
import java.math.BigInteger

@ExperimentalUnsignedTypes
class QueryParametersTests : WordSpec() {

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
                    )
                ) { v1, v2, v3, v4, v5, v6, v7, v8 ->
                    complete {
                        body(fromObject("$v1 - $v2 - $v3 - $v4 - $v5 - $v6 - $v7 - $v8"))
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
                    .uri("/blah?v1=a&v2=b&v3=3&v4=4&v5=c&v6=1&v6=2&v7=3&v7=4&v8=5&v8=6")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody(String::class.java)
                    .returnResult()
                    .responseBody shouldBe "a - Some(b) - 3 - Some(4) - c - [1, 2] - Some([3, 4]) - [5, 6]"
            }

            "extract the required parameters and those with default values if optional parameters are missing" {
                client.get()
                    .uri("/blah?v1=a&v3=3&v6=1&v6=2")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody(String::class.java)
                    .returnResult()
                    .responseBody shouldBe "a - None - 3 - None - default - [1, 2] - None - [9, 8, 7]"
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
                            complete {
                                contentType(MediaType.APPLICATION_JSON).body(fromObject(test))
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
                            complete {
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
                forall(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::stringParam, testMode, "abc")
                }
            }

            "extract a int query parameter" {
                forall(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::intParam, testMode, Int.MAX_VALUE)
                }
            }

            "extract a short query parameter" {
                forall(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::shortParam, testMode, Short.MAX_VALUE)
                }
            }

            "extract a long query parameter" {
                forall(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::longParam, testMode, Long.MAX_VALUE)
                }
            }

            "extract a byte query parameter" {
                forall(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::byteParam, testMode, Byte.MAX_VALUE)
                }
            }

            "extract a double query parameter" {
                forall(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::doubleParam, testMode, Double.MAX_VALUE)
                }
            }

            "extract a float query parameter" {
                forall(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::floatParam, testMode, Float.MAX_VALUE)
                }
            }

            "extract a BigDecimal query parameter" {
                forall(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(
                        String::bigDecimalParam,
                        testMode,
                        BigDecimal.valueOf(Double.MAX_VALUE).add(1.toBigDecimal())
                    )
                }
            }

            "extract a BigInteger query parameter" {
                forall(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(
                        String::bigIntegerParam,
                        testMode,
                        BigInteger.valueOf(Long.MAX_VALUE).add(1.toBigInteger())
                    )
                }
            }

            "extract a boolean query parameter" {
                forall(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::booleanParam, testMode, true)
                }
            }

            "extract a uInt query parameter" {
                forall(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::uIntParam, testMode, UInt.MAX_VALUE)
                }
            }

            "extract a uLong query parameter" {
                forall(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::uLongParam, testMode, ULong.MAX_VALUE)
                }
            }

            "extract a uByte query parameter" {
                forall(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::uByteParam, testMode, UByte.MAX_VALUE)
                }
            }

            "extract a uShort query parameter" {
                forall(*TestMode.rows) { testMode ->
                    testTypedQueryParameter(String::uShortParam, testMode, UShort.MAX_VALUE)
                }
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
        OPTIONAL_REPEATED_MISSING_DEFAULT;

        companion object {
            val rows = TestMode.values().map { row(it) }.toTypedArray()
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
                    .responseBody shouldBe testConfig.second.toString()
            },
            { GET("/test", it) },
            { get().uri("/test" + if (testConfig.second is None) "" else "?test=${value.toString()}") })
    }
}