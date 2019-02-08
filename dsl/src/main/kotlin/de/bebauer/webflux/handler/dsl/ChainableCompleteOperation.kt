package de.bebauer.webflux.handler.dsl

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * Chainable complete operation. Combine multiple operations with the 'or' function.
 */
abstract class ChainableCompleteOperation : BaseCompleteOperation() {
    override val response: Mono<ServerResponse>
        get() = combine(None)

    /**
     * Build the [Mono]<[ServerResponse]> with a fallback.
     *
     * @param maybeFallback the optional fallback function
     */
    abstract fun buildResponseWithFallback(maybeFallback: Option<() -> Mono<ServerResponse>>): Mono<ServerResponse>

    internal fun combine(maybeFallback: Option<() -> Mono<ServerResponse>>): Mono<ServerResponse> =
        parent.map { parent ->
            parent.combine(Some({
                buildResponseWithFallback(maybeFallback)
            }))
        }.getOrElse {
            buildResponseWithFallback(maybeFallback)
        }

    /**
     * Respond with another complete operation if this one produces no result.
     *
     * @param T the type of the other completion
     * @param other the other completion
     */
    infix fun <T : BaseCompleteOperation> or(other: T): T {
        other.parent = Some(this)
        return other
    }
}