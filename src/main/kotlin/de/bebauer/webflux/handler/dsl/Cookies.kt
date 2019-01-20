package de.bebauer.webflux.handler.dsl

import arrow.core.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

/**
 * Represents a cookie name.
 *
 * @param T type of the cookie value
 * @param U the type of the conversion result
 * @param name the name of the cookie
 * @param converter the value converter function
 * @param valueExtractor value extractor function
 */
data class CookieName<T, U>(
    val name: String,
    val converter: (List<String>) -> U,
    val valueExtractor: (Option<List<String>>) -> Either<Throwable, T>
)

/**
 * Creates a [CookieName] from a [String].
 *
 * @param T the type of the cookie value
 * @param converter the value converter function
 */
fun <T> String.cookieName(converter: (List<String>) -> T) = CookieName(this, converter) {
    when (it) {
        is None -> Left(
            ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Missing required cookie $this."
            )
        )
        is Some -> {
            if (it.t.isEmpty()) {
                Left(
                    ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Missing required cookie $this."
                    )
                )
            } else {
                Right(converter(it.t))
            }
        }
    }
}

/**
 * Makes a [CookieName] optional.
 *
 * @param T the type of the cookie value
 * @param U the type of the conversion result
 */
val <T, U> CookieName<T, U>.optional: CookieName<Option<T>, U>
    get() = CookieName(this.name, this.converter) {
        val value = this.valueExtractor(it)
        when (value) {
            is Either.Left -> Right(None)
            is Either.Right -> value.map { v -> v.toOption() }
        }
    }

/**
 * Makes a [CookieName] optional with a default value.
 *
 * @param T the type of the cookie value
 * @param U the type of the conversion result
 * @param defaultValue the default value if the header is missing
 */
fun <T, U> CookieName<T, U>.optional(defaultValue: T): CookieName<T, U> = CookieName(this.name, this.converter) {
    val value = this.valueExtractor(it)
    when (value) {
        is Either.Left -> Right(defaultValue)
        is Either.Right -> value
    }
}

/**
 * Makes a [CookieName] nullable.
 *
 * @param T the type of the cookie value
 * @param U the type of the conversion result
 */
val <T, U> CookieName<T, U>.nullable: CookieName<T?, U>
    get() = CookieName(this.name, this.converter) {
        val value = this.valueExtractor(it)
        when (value) {
            is Either.Left -> Right(null)
            is Either.Right -> value
        }
    }

/**
 * Creates a [CookieName] that only extracts the first value.
 */
val <T, U> CookieName<out List<T>, out List<U>>.single: CookieName<U, U>
    get() = this.name.cookieName { this.converter(it).first() }

/**
 * Creates a required [CookieName] that returns the value as list of strings.
 */
val String.stringCookie: CookieName<List<String>, List<String>>
    get() = this.cookieName { it }

/**
 * Extract a cookie from the [org.springframework.web.reactive.function.server.ServerRequest].
 *
 * Example:
 * ```
 * handler {
 *  cookie("test".stringCookie()) { (value) ->
 *      complete(value)
 *  }
 * }
 * ```
 *
 * @param cookie the name of the cookie as a [CookieName]
 */
fun <T, U> HandlerDsl.cookie(cookie: CookieName<T, U>, init: HandlerDsl.(T) -> CompleteOperation) = extractRequest { request ->
    val values = cookie.valueExtractor(request.cookies()[cookie.name].toOption().map { v -> v.map { it.value } })

    when (values) {
        is Either.Left -> failWith(values.a)
        is Either.Right -> init(values.b)
    }
}
