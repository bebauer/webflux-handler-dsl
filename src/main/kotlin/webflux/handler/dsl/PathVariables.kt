package webflux.handler.dsl

import arrow.core.Failure
import arrow.core.Success
import arrow.core.Try
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

/**
 * Represents a path variable.
 *
 * @param T type of the path variable
 * @param name the name of the variable
 * @param converter converter function that maps a [String] to the type of the path variable
 */
data class PathVariable<T>(val name: String, val converter: (String) -> T)

/**
 * Creates a [PathVariable] from a [String].
 *
 * @param T type of the path variable
 * @param converter converter function that maps a [String] to the type of the path variable
 */
fun <T> String.pathVariable(converter: (String) -> T) = PathVariable(this, converter)

/**
 * Creates a [String] path variable from a [String].
 */
fun String.stringVar() = this.pathVariable { it }

/**
 * Creates a [Int] path variable from a [String].
 */
fun String.intVar() = this.pathVariable(String::toInt)

/**
 * Creates a [Double] path variable from a [String].
 */
fun String.doubleVar() = this.pathVariable(String::toDouble)

/**
 * Creates a [java.math.BigDecimal] path variable from a [String].
 */
fun String.bigDecimalVar() = this.pathVariable(String::toBigDecimal)

/**
 * Creates a [java.math.BigInteger] path variable from a [String].
 */
fun String.bigIntegerVar() = this.pathVariable(String::toBigInteger)

/**
 * Creates a [Boolean] path variable from a [String].
 */
fun String.booleanVar() = this.pathVariable(String::toBoolean)

/**
 * Creates a [Byte] path variable from a [String].
 */
fun String.byteVar() = this.pathVariable(String::toByte)

/**
 * Creates a [Float] path variable from a [String].
 */
fun String.floatVar() = this.pathVariable(String::toFloat)

/**
 * Creates a [Long] path variable from a [String].
 */
fun String.longVar() = this.pathVariable(String::toLong)

/**
 * Creates a [Short] path variable from a [String].
 */
fun String.shortVar() = this.pathVariable(String::toShort)

/**
 * Creates a [UByte] path variable from a [String].
 */
@ExperimentalUnsignedTypes
fun String.uByteVar() = this.pathVariable(String::toUByte)

/**
 * Creates a [UInt] path variable from a [String].
 */
@ExperimentalUnsignedTypes
fun String.uIntVar() = this.pathVariable(String::toUInt)

/**
 * Creates a [ULong] path variable from a [String].
 */
@ExperimentalUnsignedTypes
fun String.uLongVar() = this.pathVariable(String::toULong)

/**
 * Creates a [UShort] path variable from a [String].
 */
@ExperimentalUnsignedTypes
fun String.uShortVar() = this.pathVariable(String::toUShort)

/**
 * Extracts path variables from the [org.springframework.web.reactive.function.server.ServerRequest].
 *
 * Example:
 * ```
 * handler {
 *  pathVariable("myPath".intVar()) { myInt ->
 *      complete(myInt + 1)
 *  }
 * }
 * ```
 *
 * @see HandlerDsl
 */
fun <T> HandlerDsl.pathVariable(
    variable: PathVariable<T>,
    init: HandlerDsl.(T) -> Unit
) = extractRequest { request ->
    val (name, converter) = variable

    val value = request.pathVariable(name)

    val converted = Try { converter(value) }

    when (converted) {
        is Failure -> failWith(
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to parse path variable $name.", converted.exception)
        )
        is Success -> init(converted.value)
    }
}

/**
 * Alias for [pathVariable].
 *
 * @see pathVariable
 */
fun <T1> HandlerDsl.pathVariables(
    variable1: PathVariable<T1>,
    init: HandlerDsl.(T1) -> Unit
) = pathVariable(variable1, init)