package de.bebauer.webflux.handler.dsl

import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class ResponseBuilderCompleteOperation(override val responseBuilder: () -> Mono<ServerResponse>) :
    TerminatingCompleteOperation()