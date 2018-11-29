package webflux.handler.dsl

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.toOption
import org.springframework.http.HttpCookie
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

fun HandlerDsl.cookie(name: String, init: HandlerDsl.(List<HttpCookie>) -> Unit) = nest { request ->
    val cookies = request.cookies()[name].toOption()

    when (cookies) {
        is None -> throw ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                 "Missing cookie with name $name.")
        is Some -> init(cookies.t)
    }
}


fun HandlerDsl.optionalCookie(name: String,
                              init: HandlerDsl.(Option<List<HttpCookie>>) -> Unit) = nest { request ->
    init(request.cookies()[name].toOption())
}