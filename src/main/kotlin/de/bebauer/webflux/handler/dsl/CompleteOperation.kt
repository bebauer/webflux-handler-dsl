package de.bebauer.webflux.handler.dsl

import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * Represents an operation, that completes the handler.
 */
interface CompleteOperation {

    /**
     * The response this operation returns
     */
    val response: Mono<ServerResponse>
}
