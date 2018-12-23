package de.bebauer.webflux.handler.dsl

import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class ResponseCompleteOperation(private val resp: Mono<ServerResponse>) : TerminatingCompleteOperation() {
    override val responseBuilder: () -> Mono<ServerResponse>
        get() = { resp }
}