package de.bebauer.webflux.handler.dsl

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

abstract class ChainableCompleteOperation : BaseCompleteOperation() {
    override val response: Mono<ServerResponse>
        get() = combine(None)

    abstract fun buildResponseWithFallback(maybeFallback: Option<() -> Mono<ServerResponse>>): Mono<ServerResponse>

    fun combine(maybeFallback: Option<() -> Mono<ServerResponse>>): Mono<ServerResponse> =
        parent.map { parent ->
            parent.combine(Some({
                buildResponseWithFallback(maybeFallback)
            }))
        }.getOrElse {
            buildResponseWithFallback(maybeFallback)
        }

    infix fun <T : BaseCompleteOperation> or(other: T): T {
        other.parent = Some(this)
        return other
    }
}