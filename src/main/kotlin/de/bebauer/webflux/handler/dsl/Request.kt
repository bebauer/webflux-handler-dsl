package de.bebauer.webflux.handler.dsl

import java.net.URI

/**
 * Extracts the URI from the [org.springframework.web.reactive.function.server.ServerRequest].
 */
fun HandlerDsl.extractRequestUri(init: HandlerDsl.(URI) -> Unit) = extractRequest { request ->
    init(request.uri())
}

/**
 * Extracts the host from the [org.springframework.web.reactive.function.server.ServerRequest].
 */
fun HandlerDsl.extractHost(init: HandlerDsl.(String) -> Unit) = extractRequest { request ->
    init(request.uri().host)
}

/**
 * Extracts the scheme from the [org.springframework.web.reactive.function.server.ServerRequest].
 */
fun HandlerDsl.extractScheme(init: HandlerDsl.(String) -> Unit) = extractRequest { request ->
    init(request.uri().scheme)
}