package de.bebauer.webflux.handler.dsl

import arrow.core.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

/**
 * Represents a path variable.
 *
 * @param T type of the path variable
 * @param U type of the parameter
 * @param name the name of the variable
 * @param converter converter function that maps a [String] to the type of the path variable
 * @param valueExtractor the value extraction function
 */
data class PathVariable<T, U>(
    val name: String,
    val converter: (String) -> U,
    val valueExtractor: (Option<String>) -> Either<Throwable, T>
)

/**
 * Creates a [PathVariable] from a [String].
 *
 * @param T type of the path variable
 * @param converter converter function that maps a [String] to the type of the path variable
 */
fun <T> String.pathVariable(converter: (String) -> T): PathVariable<T, T> = PathVariable(this, converter, {
    when (it) {
        is Some -> Try { converter(it.t) }.toEither().mapLeft { t ->
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
})

/**
 * Makes a [PathVariable] optional.
 *
 * @param T the type of the path variable
 * @param U type of the actual variable
 */
val <T, U> PathVariable<T, U>.optional
    get(): PathVariable<Option<T>, U> = PathVariable(name = this.name, converter = this.converter, valueExtractor = {
        when (val value = this.valueExtractor(it)) {
            is Either.Left -> Right(None)
            is Either.Right -> value.map(::Some)
        }
    })

/**
 * Makes a [PathVariable] optional.
 *
 * @param T the type of the path variable
 * @param U type of the actual variable
 * @param defaultValue the optional default value of the path variable
 */
fun <T, U> PathVariable<T, U>.optional(defaultValue: T): PathVariable<T, U> =
    PathVariable(
        this.name,
        this.converter,
        {
            when (val value = this.valueExtractor(it)) {
                is Either.Left -> Right(defaultValue)
                is Either.Right -> value
            }
        })

/**
 * Makes a [PathVariable] nullable.
 *
 * @param T the type of the path variable
 * @param U type of the actual variable
 */
val <T, U> PathVariable<T, U>.nullable
    get(): PathVariable<T?, U> =
        PathVariable(this.name, this.converter, {
            when (val value = this.valueExtractor(it)) {
                is Either.Left -> Right(null)
                is Either.Right -> value
            }
        })

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
inline fun <reified T : Enum<T>> String.enumVar(): PathVariable<T, T> = this.stringVar.toEnum()

/**
 * Maps the value conversion of a [PathVariable].
 *
 * @param T type of parameter value
 * @param U type of the target parameter value
 * @param mapper the mapping function
 */
fun <T, U> PathVariable<T, T>.map(mapper: (T) -> U): PathVariable<U, U> =
    this.name.pathVariable { value -> mapper(this.converter(value)) }

/**
 * Maps a string [PathVariable] value to upper case.
 */
val PathVariable<String, String>.toUpperCase
    get() = this.map { it.toUpperCase() }

/**
 * Maps a string [PathVariable] value to lower case.
 */
val PathVariable<String, String>.toLowerCase
    get() = this.map { it.toLowerCase() }

/**
 *  Maps a string [PathVariable] value to an enum.
 *
 *  @param T the type of the enum
 */
inline fun <reified T : Enum<T>> PathVariable<String, String>.toEnum(): PathVariable<T, T> =
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
fun <T, U> HandlerDsl.pathVariable(
    variable: PathVariable<T, U>,
    init: HandlerDsl.(T) -> CompleteOperation
) = extractRequest { request ->
    val variables = request.pathVariables()

    when (val value = variable.valueExtractor(variables[variable.name].toOption())) {
        is Either.Left -> failWith(value.a)
        is Either.Right -> init(value.b)
    }
}

/**
 * Alias for [pathVariable].
 *
 * @see pathVariable
 */
fun <T1, U1> HandlerDsl.pathVariables(
    variable1: PathVariable<T1, U1>,
    init: HandlerDsl.(T1) -> CompleteOperation
) = pathVariable(variable1, init)