package de.bebauer.webflux.handler.dsl

import arrow.core.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

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
                is Some -> Try { converter(it.t[0]) }.toEither().mapLeft { t ->
                    ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid value for query parameter $this. Conversion failed.",
                        t
                    )
                }
                is None -> Left(
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
val <T, U> QueryParameter<T, U>.optional
    get(): QueryParameter<Option<T>, U> =
        QueryParameter(
            this.name,
            this.converter,
            {
                val value = this.valueExtractor(it)
                when (value) {
                    is Either.Left -> Right(None)
                    is Either.Right -> value.map(::Some)
                }
            })

/**
 * Makes a [QueryParameter] optional.
 *
 * @param T the type of the parameter
 * @param U type of a single parameter
 * @param defaultValue the optional default value of the parameter
 */
fun <T, U> QueryParameter<T, U>.optional(defaultValue: T): QueryParameter<T, U> =
    QueryParameter(
        this.name,
        this.converter,
        {
            val value = this.valueExtractor(it)
            when (value) {
                is Either.Left -> Right(defaultValue)
                is Either.Right -> value
            }
        })

/**
 * Makes a [QueryParameter] nullable.
 *
 * @param T the type of the parameter
 * @param U type of a single parameter
 */
val <T, U> QueryParameter<T, U>.nullable
    get(): QueryParameter<T?, U> =
        QueryParameter(this.name, this.converter, {
            val value = this.valueExtractor(it)
            when (value) {
                is Either.Left -> Right(null)
                is Either.Right -> value
            }
        })

/**
 * Converts a [QueryParameter] to a repeated parameter. All occurrences of the query parameter will be extracted into a list.
 *
 * @param T the type of the parameter
 */
val <T> QueryParameter<T, T>.repeated
    get(): QueryParameter<List<T>, T> =
        QueryParameter(this.name, this.converter, {
            when (it) {
                is Some -> Right(it.t.map(converter))
                is None -> Left(
                    ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Missing required query parameter $name."
                    )
                )
            }
        })

/**
 * Creates a [String] extracting [QueryParameter].
 */
val String.stringParam
    get() = this.queryParam { it }

/**
 * Creates a [Int] extracting [QueryParameter].
 */
val String.intParam
    get() = this.queryParam(String::toInt)

/**
 * Creates a [Double] extracting [QueryParameter].
 */
val String.doubleParam
    get() = this.queryParam(String::toDouble)

/**
 * Creates a [java.math.BigDecimal] extracting [QueryParameter].
 */
val String.bigDecimalParam
    get() = this.queryParam(String::toBigDecimal)

/**
 * Creates a [java.math.BigInteger] extracting [QueryParameter].
 */
val String.bigIntegerParam
    get() = this.queryParam(String::toBigInteger)

/**
 * Creates a [Boolean] extracting [QueryParameter].
 */
val String.booleanParam
    get() = this.queryParam(String::toBoolean)

/**
 * Creates a [Byte] extracting [QueryParameter].
 */
val String.byteParam
    get() = this.queryParam(String::toByte)

/**
 * Creates a [Float] extracting [QueryParameter].
 */
val String.floatParam
    get() = this.queryParam(String::toFloat)

/**
 * Creates a [Long] extracting [QueryParameter].
 */
val String.longParam
    get() = this.queryParam(String::toLong)

/**
 * Creates a [Short] extracting [QueryParameter].
 */
val String.shortParam
    get() = this.queryParam(String::toShort)

/**
 * Creates a [UByte] extracting [QueryParameter].
 */
@ExperimentalUnsignedTypes
val String.uByteParam
    get() = this.queryParam(String::toUByte)

/**
 * Creates a [UInt] extracting [QueryParameter].
 */
@ExperimentalUnsignedTypes
val String.uIntParam
    get() = this.queryParam(String::toUInt)

/**
 * Creates a [ULong] extracting [QueryParameter].
 */
@ExperimentalUnsignedTypes
val String.uLongParam
    get() = this.queryParam(String::toULong)

/**
 * Creates a [UShort] extracting [QueryParameter].
 */
@ExperimentalUnsignedTypes
val String.uShortParam
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
val String.csvParam
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
val QueryParameter<String, String>.toUpperCase
    get() = this.map { it.toUpperCase() }

/**
 * Maps a string [QueryParameter] value to lower case.
 */
val QueryParameter<String, String>.toLowerCase
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
) = extractRequest { request ->
    val values = parameter.valueExtractor(request.queryParams()[parameter.name].toOption())

    when (values) {
        is Either.Left -> failWith(values.a)
        is Either.Right -> init(values.b)
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
) = this.parameter(parameter1, init)
