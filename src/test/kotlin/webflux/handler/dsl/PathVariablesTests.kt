package webflux.handler.dsl

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import java.math.BigDecimal
import java.math.BigInteger

@ExperimentalUnsignedTypes
class PathVariablesTests {

    @Test
    fun `string path variable`() {
        testTypedVariable(String::stringVar, "abc")
    }

    @Test
    fun `int path variable`() {
        testTypedVariable(String::intVar, Int.MAX_VALUE)
    }

    @Test
    fun `short path variable`() {
        testTypedVariable(String::shortVar, Short.MAX_VALUE)
    }

    @Test
    fun `long path variable`() {
        testTypedVariable(String::longVar, Long.MAX_VALUE)
    }

    @Test
    fun `byte path variable`() {
        testTypedVariable(String::byteVar, Byte.MAX_VALUE)
    }

    @Test
    fun `double path variable`() {
        testTypedVariable(String::doubleVar, Double.MAX_VALUE)
    }

    @Test
    fun `float path variable`() {
        testTypedVariable(String::floatVar, Float.MAX_VALUE)
    }

    @Test
    fun `BigDecimal path variable`() {
        testTypedVariable(String::bigDecimalVar, BigDecimal.valueOf(Double.MAX_VALUE).add(1.toBigDecimal()))
    }

    @Test
    fun `BigInteger path variable`() {
        testTypedVariable(String::bigIntegerVar, BigInteger.valueOf(Long.MAX_VALUE).add(1.toBigInteger()))
    }

    @Test
    fun `boolean path variable`() {
        testTypedVariable(String::booleanVar, true)
    }

    @Test
    fun `uInt path variable`() {
        testTypedVariable(String::uIntVar, UInt.MAX_VALUE)
    }

    @Test
    fun `uLong path variable`() {
        testTypedVariable(String::uLongVar, ULong.MAX_VALUE)
    }

    @Test
    fun `uByte path variable`() {
        testTypedVariable(String::uByteVar, UByte.MAX_VALUE)
    }

    @Test
    fun `uShort path variable`() {
        testTypedVariable(String::uShortVar, UShort.MAX_VALUE)
    }

    @Test
    fun `custom path variable`() {
        data class Test(val x: Int) {
            override fun toString() = x.toString()
        }

        testTypedVariable({ pathVariable { Test(it.toInt()) } }, Test(123))
    }

    @Test
    fun `multiple mixed path variables`() {
        runHandlerTest(
            handler {
                pathVariables(
                    "test1".intVar(),
                    "test2".stringVar(),
                    "test3".stringVar()
                ) { test1, test2, test3 ->
                    complete {
                        contentType(MediaType.APPLICATION_JSON)
                        body(BodyInserters.fromObject(listOf(test1, test2, test3)))
                    }
                }
            },
            {
                expectStatus().isOk
                    .expectBody(List::class.java)
                    .returnResult()
                    .apply { Assertions.assertThat(responseBody).containsExactly(123, "a", "b") }
            },
            { GET("/test/{test1}/{test2}/{test3}", it) },
            { get().uri("/test/123/a/b") })
    }

    @Test
    fun `failed to parse`() {
        runHandlerTest(
            handler {
                pathVariables("test".intVar()) { test ->
                    complete(test)
                }
            },
            {
                expectStatus().isBadRequest
            },
            { GET("/test/{test}", it) },
            { get().uri("/test/abc") })
    }

    private inline fun <reified T> testTypedVariable(
        crossinline variable: String.() -> PathVariable<T>,
        expectedResult: T
    ) {
        runHandlerTest(
            handler {
                pathVariable("test".variable()) { test ->
                    complete(test.toString())
                }
            },
            {
                expectStatus().isOk
                    .expectBody(String::class.java)
                    .returnResult().apply { Assertions.assertThat(responseBody).isEqualTo(expectedResult.toString()) }
            },
            { GET("/test/{test}", it) },
            { get().uri("/test/${expectedResult.toString()}") })
    }
}