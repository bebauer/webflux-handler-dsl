package de.bebauer.webflux.handler.dsl

import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * Complete operation which produces the response from the specified builder function.
 *
 * @param responseBuilder the response builder function
 */
class ResponseBuilderCompleteOperation(override val responseBuilder: () -> Mono<ServerResponse>) :
    TerminatingCompleteOperation()