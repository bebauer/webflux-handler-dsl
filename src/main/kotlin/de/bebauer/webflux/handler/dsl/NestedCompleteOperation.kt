package de.bebauer.webflux.handler.dsl

import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * Complete operation wrapper for a [Mono]<[CompleteOperation]>.
 *
 * @param T type of the complete operation
 */
class NestedCompleteOperation<T : CompleteOperation>(private val operation: Mono<T>) : BaseCompleteOperation() {
    override val response: Mono<ServerResponse>
        get() = operation.flatMap { it.response }

    /**
     * Transform this nested complete operation into another.
     *
     * @param U the type of the target complete operation
     * @param mapper the transformation function
     */
    fun <U : CompleteOperation> flatMap(mapper: (Mono<T>) -> Mono<U>): NestedCompleteOperation<U> =
        NestedCompleteOperation(mapper(operation))
}