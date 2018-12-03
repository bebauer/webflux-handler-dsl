package de.bebauer.webflux.handler.dsl

import java.net.URI

/**
 * Extracts the URI from the [org.springframework.web.reactive.function.server.ServerRequest].
 */
fun HandlerDsl.extractRequestUri(init: HandlerDsl.(URI) -> Unit) = extractRequest { request ->
    init(request.uri())
}