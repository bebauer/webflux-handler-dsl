package de.bebauer.webflux.handler.dsl

import arrow.core.Some
import arrow.core.getOrElse
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

abstract class TerminatingCompleteOperation : BaseCompleteOperation() {
    abstract val responseBuilder: () -> Mono<ServerResponse>

    override val response: Mono<ServerResponse>
        get() = parent.map { it.combine(Some(responseBuilder)) }
            .getOrElse { responseBuilder() }
}