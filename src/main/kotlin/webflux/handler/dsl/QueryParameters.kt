package webflux.handler.dsl

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
fun <T> String.queryParam(converter: (String) -> T): QueryParameter<T, T> = QueryParameter(
    name = this,
    converter = converter,
    valueExtractor = {
        when (it) {
            is Some -> Right(converter(it.t[0]))
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
 * @param defaultValue the optional default value of the parameter
 */
fun <T, U> QueryParameter<T, U>.optional(defaultValue: T? = null): QueryParameter<Option<T>, U> =
    QueryParameter(
        this.name,
        this.converter,
        {
            val value = this.valueExtractor(it)
            when (value) {
                is Either.Left -> Right(defaultValue.toOption())
                is Either.Right -> value.map(::Some)
            }
        })

/**
 * Converts a [QueryParameter] to a repeated parameter. All occurrences of the query parameter will be extracted into a list.
 *
 * @param T the type of the parameter
 */
fun <T> QueryParameter<T, T>.repeated(): QueryParameter<List<T>, T> =
    QueryParameter(this.name, this.converter, {
        when (it) {
            is Some -> Right(it.t.map(converter))
            is None -> Left(ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required query parameter $name."))
        }
    })

/**
 * Creates a [String] extracting [QueryParameter].
 */
fun String.stringParam() = this.queryParam { it }

/**
 * Creates a [Int] extracting [QueryParameter].
 */
fun String.intParam() = this.queryParam(String::toInt)

/**
 * Creates a [Double] extracting [QueryParameter].
 */
fun String.doubleParam() = this.queryParam(String::toDouble)

/**
 * Creates a [java.math.BigDecimal] extracting [QueryParameter].
 */
fun String.bigDecimalParam() = this.queryParam(String::toBigDecimal)

/**
 * Creates a [java.math.BigInteger] extracting [QueryParameter].
 */
fun String.bigIntegerParam() = this.queryParam(String::toBigInteger)

/**
 * Creates a [Boolean] extracting [QueryParameter].
 */
fun String.booleanParam() = this.queryParam(String::toBoolean)

/**
 * Creates a [Byte] extracting [QueryParameter].
 */
fun String.byteParam() = this.queryParam(String::toByte)

/**
 * Creates a [Float] extracting [QueryParameter].
 */
fun String.floatParam() = this.queryParam(String::toFloat)

/**
 * Creates a [Long] extracting [QueryParameter].
 */
fun String.longParam() = this.queryParam(String::toLong)

/**
 * Creates a [Short] extracting [QueryParameter].
 */
fun String.shortParam() = this.queryParam(String::toShort)

/**
 * Creates a [UByte] extracting [QueryParameter].
 */
@ExperimentalUnsignedTypes
fun String.uByteParam() = this.queryParam(String::toUByte)

/**
 * Creates a [UInt] extracting [QueryParameter].
 */
@ExperimentalUnsignedTypes
fun String.uIntParam() = this.queryParam(String::toUInt)

/**
 * Creates a [ULong] extracting [QueryParameter].
 */
@ExperimentalUnsignedTypes
fun String.uLongParam() = this.queryParam(String::toULong)

/**
 * Creates a [UShort] extracting [QueryParameter].
 */
@ExperimentalUnsignedTypes
fun String.uShortParam() = this.queryParam(String::toUShort)

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
fun String.csvParam() = this.csvParam { it }

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
    init: HandlerDsl.(T) -> Unit
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
    init: HandlerDsl.(T1) -> Unit
) = this.parameter(parameter1, init)
