package de.bebauer.webflux.handler.dsl

import java.net.URI

/**
 * Extracts the URI from the [org.springframework.web.reactive.function.server.ServerRequest].
 */
fun HandlerDsl.extractRequestUri(init: HandlerDsl.(URI) -> CompleteOperation): CompleteOperation =
    extractRequest { request ->
        init(request.uri())
    }

/**
 * Extracts the host from the [org.springframework.web.reactive.function.server.ServerRequest].
 */
fun HandlerDsl.extractHost(init: HandlerDsl.(String) -> CompleteOperation): CompleteOperation =
    extractRequest { request ->
        init(request.uri().host)
    }

/**
 * Extracts the scheme from the [org.springframework.web.reactive.function.server.ServerRequest].
 */
fun HandlerDsl.extractScheme(init: HandlerDsl.(String) -> CompleteOperation): CompleteOperation =
    extractRequest { request ->
        init(request.uri().scheme)
    }