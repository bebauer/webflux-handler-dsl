package de.bebauer.webflux.handler.dsl

import arrow.core.Some
import arrow.core.getOrElse
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * Terminating complete operation. This one allows no further chaining of completions.
 */
abstract class TerminatingCompleteOperation : BaseCompleteOperation() {
    /**
     * Response builder function that produces the [Mono]<[ServerResponse]>.
     */
    abstract val responseBuilder: () -> Mono<ServerResponse>

    override val response: Mono<ServerResponse>
        get() = parent.map { it.combine(Some(responseBuilder)) }
            .getOrElse { responseBuilder() }
}