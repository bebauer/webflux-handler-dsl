package de.bebauer.webflux.handler.dsl

import arrow.core.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Represents a query parameter.
 *
 * @param T type of parameter value
 * @param U type of a single parameter
 * @param name the name of the parameter
 * @param converter the converter which maps a single occurrence of the parameter to it's type
 * @param valueExtractor the value extraction function
 */
data class QueryParameter<T, U>(
    val name: String,
    val converter: (String) -> U,
    val valueExtractor: (Option<List<String>>) -> Either<Throwable, T>
)

/**
 * Creates a [QueryParameter] from a [String].
 *
 * @param T the type of the parameter
 * @param converter the converter function mapping a [String] to the parameter type
 */
fun <T> String.queryParam(converter: (String) -> T): QueryParameter<T, T> =
    QueryParameter(
        name = this,
        converter = converter,
        valueExtractor = {
            when (it) {
                is Some -> try {
                    Either.Right(converter(it.value[0]))
                } catch (t: Throwable) {
                    Either.Left(
                        ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid value for query parameter $this. Conversion failed.",
                            t
                        )
                    )
                }
                is None -> Either.Left(
                    ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Missing required query parameter $this."
                    )
                )
            }
        }
    )

/**
 * Makes a [QueryParameter] optional.
 *
 * @param T the type of the parameter
 * @param U type of a single parameter
 */
val <T, U> QueryParameter<T, U>.optional: QueryParameter<Option<T>, U>
    get() = QueryParameter(this.name, this.converter) {
        when (val value = this.valueExtractor(it)) {
            is Either.Left -> Either.Right(None)
            is Either.Right -> value.map(::Some)
        }
    }

/**
 * Makes a [QueryParameter] optional.
 *
 * @param T the type of the parameter
 * @param U type of a single parameter
 * @param defaultValue the optional default value of the parameter
 */
fun <T, U> QueryParameter<T, U>.optional(defaultValue: T): QueryParameter<T, U> =
    QueryParameter(this.name, this.converter) {
        when (val value = this.valueExtractor(it)) {
            is Either.Left -> Either.Right(defaultValue)
            is Either.Right -> value
        }
    }

/**
 * Makes a [QueryParameter] nullable.
 *
 * @param T the type of the parameter
 * @param U type of a single parameter
 */
val <T, U> QueryParameter<T, U>.nullable: QueryParameter<T?, U>
    get() = QueryParameter(this.name, this.converter) {
        when (val value = this.valueExtractor(it)) {
            is Either.Left -> Either.Right(null)
            is Either.Right -> value
        }
    }

/**
 * Converts a [QueryParameter] to a repeated parameter. All occurrences of the query parameter will be extracted into a list.
 *
 * @param T the type of the parameter
 */
val <T> QueryParameter<T, T>.repeated: QueryParameter<List<T>, T>
    get() = QueryParameter(this.name, this.converter) {
        when (it) {
            is Some -> Either.Right(it.value.map(converter))
            is None -> Either.Left(
                ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Missing required query parameter $name."
                )
            )
        }
    }

/**
 * Creates a [String] extracting [QueryParameter].
 */
val String.stringParam: QueryParameter<String, String>
    get() = this.queryParam { it }

/**
 * Creates a [Int] extracting [QueryParameter].
 */
val String.intParam: QueryParameter<Int, Int>
    get() = this.queryParam(String::toInt)

/**
 * Creates a [Double] extracting [QueryParameter].
 */
val String.doubleParam: QueryParameter<Double, Double>
    get() = this.queryParam(String::toDouble)

/**
 * Creates a [java.math.BigDecimal] extracting [QueryParameter].
 */
val String.bigDecimalParam: QueryParameter<BigDecimal, BigDecimal>
    get() = this.queryParam(String::toBigDecimal)

/**
 * Creates a [java.math.BigInteger] extracting [QueryParameter].
 */
val String.bigIntegerParam: QueryParameter<BigInteger, BigInteger>
    get() = this.queryParam(String::toBigInteger)

/**
 * Creates a [Boolean] extracting [QueryParameter].
 */
val String.booleanParam: QueryParameter<Boolean, Boolean>
    get() = this.queryParam(String::toBoolean)

/**
 * Creates a [Byte] extracting [QueryParameter].
 */
val String.byteParam: QueryParameter<Byte, Byte>
    get() = this.queryParam(String::toByte)

/**
 * Creates a [Float] extracting [QueryParameter].
 */
val String.floatParam: QueryParameter<Float, Float>
    get() = this.queryParam(String::toFloat)

/**
 * Creates a [Long] extracting [QueryParameter].
 */
val String.longParam: QueryParameter<Long, Long>
    get() = this.queryParam(String::toLong)

/**
 * Creates a [Short] extracting [QueryParameter].
 */
val String.shortParam: QueryParameter<Short, Short>
    get() = this.queryParam(String::toShort)

/**
 * Creates a [UByte] extracting [QueryParameter].
 */
@ExperimentalUnsignedTypes
val String.uByteParam: QueryParameter<UByte, UByte>
    get() = this.queryParam(String::toUByte)

/**
 * Creates a [UInt] extracting [QueryParameter].
 */
@ExperimentalUnsignedTypes
val String.uIntParam: QueryParameter<UInt, UInt>
    get() = this.queryParam(String::toUInt)

/**
 * Creates a [ULong] extracting [QueryParameter].
 */
@ExperimentalUnsignedTypes
val String.uLongParam: QueryParameter<ULong, ULong>
    get() = this.queryParam(String::toULong)

/**
 * Creates a [UShort] extracting [QueryParameter].
 */
@ExperimentalUnsignedTypes
val String.uShortParam: QueryParameter<UShort, UShort>
    get() = this.queryParam(String::toUShort)

/**
 * Creates a query parameter that extracts comma separated values.
 *
 * @param T the type of the parameter
 * @param converter the converter function mapping a [String] to the parameter type
 */
fun <T> String.csvParam(converter: (String) -> T): QueryParameter<List<T>, List<T>> = this.queryParam { value ->
    value.split(",").map(converter)
}

/**
 * Creates a query parameter that extracts comma separated [String] values.
 */
val String.csvParam: QueryParameter<List<String>, List<String>>
    get() = this.csvParam { it }

/**
 * Creates an enum extracting [QueryParameter].
 *
 * @param T the type of the enum
 */
inline fun <reified T : Enum<T>> String.enumParam(): QueryParameter<T, T> = this.stringParam.toEnum()

/**
 * Maps the value conversion of a [QueryParameter].
 *
 * @param T type of parameter value
 * @param U type of the target parameter value
 * @param mapper the mapping function
 */
fun <T, U> QueryParameter<T, T>.map(mapper: (T) -> U): QueryParameter<U, U> =
    this.name.queryParam { value -> mapper(this.converter(value)) }

/**
 * Maps a string [QueryParameter] value to upper case.
 */
val QueryParameter<String, String>.toUpperCase: QueryParameter<String, String>
    get() = this.map { it.toUpperCase() }

/**
 * Maps a string [QueryParameter] value to lower case.
 */
val QueryParameter<String, String>.toLowerCase: QueryParameter<String, String>
    get() = this.map { it.toLowerCase() }

/**
 *  Maps a string [QueryParameter] value to an enum.
 *
 *  @param T the type of the enum
 */
inline fun <reified T : Enum<T>> QueryParameter<String, String>.toEnum(): QueryParameter<T, T> =
    this.map { java.lang.Enum.valueOf(T::class.java, it) }

/**
 * Extracts query parameters from the [org.springframework.web.reactive.function.server.ServerRequest].
 *
 * Example:
 * ```
 * handler {
 *  parameter("myParam".stringParam().repeated().optional()) { myParam -> // Option<List<String>>
 *      complete(myParam.getOrElse(List.empty()).joinToString())
 *  }
 * }
 * ```
 *
 * @see HandlerDsl
 */
fun <T, U> HandlerDsl.parameter(
    parameter: QueryParameter<T, U>,
    init: HandlerDsl.(T) -> CompleteOperation
): CompleteOperation = extractRequest { request ->
    when (val values = parameter.valueExtractor(request.queryParams()[parameter.name].toOption())) {
        is Either.Left -> failWith(values.value)
        is Either.Right -> init(values.value)
    }
}

/**
 * Alias for [parameter].
 *
 * @see parameter
 */
fun <T1, U1> HandlerDsl.parameters(
    parameter1: QueryParameter<T1, U1>,
    init: HandlerDsl.(T1) -> CompleteOperation
): CompleteOperation = this.parameter(parameter1, init)
