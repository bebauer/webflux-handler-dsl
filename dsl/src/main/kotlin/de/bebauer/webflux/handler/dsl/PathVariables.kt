package de.bebauer.webflux.handler.dsl

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
val String.stringVar
    get() = this.pathVariable { it }

/**
 * Creates a [Int] path variable from a [String].
 */
val String.intVar
    get() = this.pathVariable(String::toInt)

/**
 * Creates a [Double] path variable from a [String].
 */
val String.doubleVar
    get() = this.pathVariable(String::toDouble)

/**
 * Creates a [java.math.BigDecimal] path variable from a [String].
 */
val String.bigDecimalVar
    get() = this.pathVariable(String::toBigDecimal)

/**
 * Creates a [java.math.BigInteger] path variable from a [String].
 */
val String.bigIntegerVar
    get() = this.pathVariable(String::toBigInteger)

/**
 * Creates a [Boolean] path variable from a [String].
 */
val String.booleanVar
    get() = this.pathVariable(String::toBoolean)

/**
 * Creates a [Byte] path variable from a [String].
 */
val String.byteVar
    get() = this.pathVariable(String::toByte)

/**
 * Creates a [Float] path variable from a [String].
 */
val String.floatVar
    get() = this.pathVariable(String::toFloat)

/**
 * Creates a [Long] path variable from a [String].
 */
val String.longVar
    get() = this.pathVariable(String::toLong)

/**
 * Creates a [Short] path variable from a [String].
 */
val String.shortVar
    get() = this.pathVariable(String::toShort)

/**
 * Creates a [UByte] path variable from a [String].
 */
@ExperimentalUnsignedTypes
val String.uByteVar
    get() = this.pathVariable(String::toUByte)

/**
 * Creates a [UInt] path variable from a [String].
 */
@ExperimentalUnsignedTypes
val String.uIntVar
    get() = this.pathVariable(String::toUInt)

/**
 * Creates a [ULong] path variable from a [String].
 */
@ExperimentalUnsignedTypes
val String.uLongVar
    get() = this.pathVariable(String::toULong)

/**
 * Creates a [UShort] path variable from a [String].
 */
@ExperimentalUnsignedTypes
val String.uShortVar
    get() = this.pathVariable(String::toUShort)


/**
 * Creates an enum extracting [PathVariable].
 *
 * @param T the type of the enum
 */
inline fun <reified T : Enum<T>> String.enumVar(): PathVariable<T> = this.stringVar.toEnum()

/**
 * Maps the value conversion of a [PathVariable].
 *
 * @param T type of parameter value
 * @param U type of the target parameter value
 * @param mapper the mapping function
 */
fun <T, U> PathVariable<T>.map(mapper: (T) -> U): PathVariable<U> =
    this.name.pathVariable { value -> mapper(this.converter(value)) }

/**
 * Maps a string [PathVariable] value to upper case.
 */
val PathVariable<String>.toUpperCase
    get() = this.map { it.toUpperCase() }

/**
 * Maps a string [PathVariable] value to lower case.
 */
val PathVariable<String>.toLowerCase
    get() = this.map { it.toLowerCase() }

/**
 *  Maps a string [PathVariable] value to an enum.
 *
 *  @param T the type of the enum
 */
inline fun <reified T : Enum<T>> PathVariable<String>.toEnum(): PathVariable<T> =
    this.map { java.lang.Enum.valueOf(T::class.java, it) }

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
    init: HandlerDsl.(T) -> CompleteOperation
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
    init: HandlerDsl.(T1) -> CompleteOperation
) = pathVariable(variable1, init)