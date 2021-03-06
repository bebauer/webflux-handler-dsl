package de.bebauer.webflux.handler.dsl

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import java.math.BigDecimal
import java.math.BigInteger

@ExperimentalUnsignedTypes
class PathVariablesTests : WordSpec() {

    enum class TestEnum {
        FIRST,

        @Suppress("EnumEntryName")
        second,
        Third
    }

    init {
        "pathVariable" should {
            "extract string path variable" {
                testTypedVariable(String::stringVar, "abc")
            }

            "extract int path variable" {
                testTypedVariable(String::intVar, Int.MAX_VALUE)
            }

            "extract short path variable" {
                testTypedVariable(String::shortVar, Short.MAX_VALUE)
            }

            "extract long path variable" {
                testTypedVariable(String::longVar, Long.MAX_VALUE)
            }

            "extract byte path variable" {
                testTypedVariable(String::byteVar, Byte.MAX_VALUE)
            }

            "extract double path variable" {
                testTypedVariable(String::doubleVar, Double.MAX_VALUE)
            }

            "extract float path variable" {
                testTypedVariable(String::floatVar, Float.MAX_VALUE)
            }

            "extract BigDecimal path variable" {
                testTypedVariable(String::bigDecimalVar, BigDecimal.valueOf(Double.MAX_VALUE).add(1.toBigDecimal()))
            }

            "extract BigInteger path variable" {
                testTypedVariable(String::bigIntegerVar, BigInteger.valueOf(Long.MAX_VALUE).add(1.toBigInteger()))
            }

            "extract boolean path variable" {
                testTypedVariable(String::booleanVar, true)
            }

            "extract uInt path variable" {
                testTypedVariable(String::uIntVar, UInt.MAX_VALUE)
            }

            "extract uLong path variable" {
                testTypedVariable(String::uLongVar, ULong.MAX_VALUE)
            }

            "extract uByte path variable" {
                testTypedVariable(String::uByteVar, UByte.MAX_VALUE)
            }

            "extract uShort path variable" {
                testTypedVariable(String::uShortVar, UShort.MAX_VALUE)
            }

            "extract enum variable" {
                testTypedVariable({ this.enumVar() }, TestEnum.Third)
            }

            "extract upper case string enum variable" {
                testTypedVariable({ this.stringVar.toUpperCase.toEnum() }, TestEnum.FIRST, { "fiRst" })
            }

            "extract lower case string enum variable" {
                testTypedVariable({ this.stringVar.toLowerCase.toEnum() }, TestEnum.second, { "SecOnd" })
            }

            "extract exact string enum variable" {
                testTypedVariable({ this.stringVar.toEnum() }, TestEnum.Third)
            }

            "extract nullable path variable" {
                runHandlerTest(
                    handler {
                        pathVariable("test".stringVar.nullable) { test ->
                            ok(test ?: "null")
                        }
                    },
                    {
                        expectStatus().isOk
                            .expectBody(String::class.java)
                            .returnResult().responseBody shouldBe "abc"
                    },
                    { GET("/test/{test}", it) },
                    { get().uri("/test/abc") })
            }

            "extract missing nullable path variable" {
                runHandlerTest(
                    handler {
                        pathVariable("test".stringVar.nullable) { test ->
                            ok(test ?: "null")
                        }
                    },
                    {
                        expectStatus().isOk
                            .expectBody(String::class.java)
                            .returnResult().responseBody shouldBe "null"
                    },
                    { GET("/test", it) },
                    { get().uri("/test") })
            }

            "extract optional path variable" {
                runHandlerTest(
                    handler {
                        pathVariable("test".stringVar.optional) { test ->
                            ok(test.toString())
                        }
                    },
                    {
                        expectStatus().isOk
                            .expectBody(String::class.java)
                            .returnResult().responseBody shouldBe "Option.Some(abc)"
                    },
                    { GET("/test/{test}", it) },
                    { get().uri("/test/abc") })
            }

            "extract missing optional path variable" {
                runHandlerTest(
                    handler {
                        pathVariable("test".stringVar.optional) { test ->
                            ok(test.toString())
                        }
                    },
                    {
                        expectStatus().isOk
                            .expectBody(String::class.java)
                            .returnResult().responseBody shouldBe "Option.None"
                    },
                    { GET("/test", it) },
                    { get().uri("/test") })
            }

            "extract optional path variable with default" {
                runHandlerTest(
                    handler {
                        pathVariable("test".stringVar.optional("xyz")) { test ->
                            ok(test)
                        }
                    },
                    {
                        expectStatus().isOk
                            .expectBody(String::class.java)
                            .returnResult().responseBody shouldBe "abc"
                    },
                    { GET("/test/{test}", it) },
                    { get().uri("/test/abc") })
            }

            "extract missing optional path variable with default" {
                runHandlerTest(
                    handler {
                        pathVariable("test".stringVar.optional("xyz")) { test ->
                            ok(test)
                        }
                    },
                    {
                        expectStatus().isOk
                            .expectBody(String::class.java)
                            .returnResult().responseBody shouldBe "xyz"
                    },
                    { GET("/test", it) },
                    { get().uri("/test") })
            }

            "extract custom path variable" {
                data class Test(val x: Int) {
                    override fun toString() = x.toString()
                }

                testTypedVariable({ pathVariable { Test(it.toInt()) } }, Test(123))
            }

            "extract multiple mixed path variables" {
                runHandlerTest(
                    handler {
                        pathVariables(
                            "test1".intVar,
                            "test2".stringVar,
                            "test3".stringVar
                        ) { test1, test2, test3 ->
                            ok {
                                contentType(MediaType.APPLICATION_JSON)
                                body(BodyInserters.fromValue(listOf(test1, test2, test3)))
                            }
                        }
                    },
                    {
                        expectStatus().isOk
                            .expectBodyList(Any::class.java)
                            .returnResult().responseBody should containExactly(123, "a", "b")
                    },
                    { GET("/test/{test1}/{test2}/{test3}", it) },
                    { get().uri("/test/123/a/b") })
            }

            "return with bad request if variable fails to parse the passed value" {
                runHandlerTest(
                    handler {
                        pathVariables("test".intVar) { test ->
                            ok(test)
                        }
                    },
                    {
                        expectStatus().isBadRequest
                    },
                    { GET("/test/{test}", it) },
                    { get().uri("/test/abc") })
            }
        }
    }

    private inline fun <reified T> testTypedVariable(
        crossinline variable: String.() -> PathVariable<T, T>,
        expectedResult: T,
        crossinline value: (T) -> String = { it.toString() }
    ) {
        runHandlerTest(
            handler {
                pathVariable("test".variable()) { test ->
                    ok(test.toString())
                }
            },
            {
                expectStatus().isOk
                    .expectBody(String::class.java)
                    .returnResult().responseBody shouldBe expectedResult.toString()
            },
            { GET("/test/{test}", it) },
            { get().uri("/test/${value(expectedResult)}") })
    }
}