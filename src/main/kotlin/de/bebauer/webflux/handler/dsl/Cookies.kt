package de.bebauer.webflux.handler.dsl

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.toOption
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

/**
 * Extract a cookie from the [org.springframework.web.reactive.function.server.ServerRequest].
 *
 * Example:
 * ```
 * handler {
 *  cookie("test) { (value) ->
 *      complete(value)
 *  }
 * }
 * ```
 *
 * @param name the name of the cookie
 */
fun HandlerDsl.cookie(name: String, init: HandlerDsl.(List<String>) -> Unit) = extractRequest { request ->
    val cookies = request.cookies()[name].toOption()

    when (cookies) {
        is None -> failWith(ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing cookie with name $name."))
        is Some -> init(cookies.t.map { it.value })
    }
}

/**
 * Extract an optional cookie from the [org.springframework.web.reactive.function.server.ServerRequest].
 *
 * Example:
 * ```
 * handler {
 *  optionalCookie("test) { (value) ->
 *      value.map { complete(it) }.getOrElse { failWith("missing") }
 *  }
 * }
 * ```
 *
 * @param name the name of the cookie
 */
fun HandlerDsl.optionalCookie(
    name: String,
    init: HandlerDsl.(Option<List<String>>) -> Unit
) = extractRequest { request ->
    init(request.cookies()[name].toOption().map { list -> list.map { it.value } })
}