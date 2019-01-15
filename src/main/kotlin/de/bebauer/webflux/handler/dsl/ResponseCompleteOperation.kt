package de.bebauer.webflux.handler.dsl

import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * Complete operation from a concrete [Mono]<[ServerResponse]>.
 *
 * @param resp the server response
 */
class ResponseCompleteOperation(private val resp: Mono<ServerResponse>) : TerminatingCompleteOperation() {
    override val responseBuilder: () -> Mono<ServerResponse>
        get() = { resp }
}