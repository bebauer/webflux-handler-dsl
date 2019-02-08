package de.bebauer.webflux.handler.dsl

import io.kotlintest.matchers.collections.containExactly
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import java.math.BigDecimal
import java.math.BigInteger

@ExperimentalUnsignedTypes
class PathVariablesTests : WordSpec() {

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
                                body(BodyInserters.fromObject(listOf(test1, test2, test3)))
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
        crossinline variable: String.() -> PathVariable<T>,
        expectedResult: T
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
            { get().uri("/test/${expectedResult.toString()}") })
    }
}