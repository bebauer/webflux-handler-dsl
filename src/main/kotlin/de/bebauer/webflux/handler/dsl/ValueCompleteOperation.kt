package de.bebauer.webflux.handler.dsl

import arrow.core.Option
import arrow.core.getOrElse
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * Complete operation, from an optional value.
 *
 * @param T type of the body element
 * @param status the response status
 * @param maybeValue the value [Option]
 * @param builderInit block for customizing the response
 */
class ValueCompleteOperation<T>(
    private val status: HttpStatus,
    private val maybeValue: Option<T>,
    private val builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
) : ChainableCompleteOperation() {
    override fun buildResponseWithFallback(maybeFallback: Option<() -> Mono<ServerResponse>>): Mono<ServerResponse> =
        maybeValue.map { ServerResponse.status(status).builderInit().body(BodyInserters.fromObject(it)) }
            .getOrElse {
                maybeFallback.map { it() }.getOrElse { ServerResponse.status(status).builderInit().build() }
            }

    /**
     * Map the body value.
     *
     * @param U type of the mapping result
     * @param mapper the mapping function
     */
    fun <U> map(mapper: (T) -> U): ValueCompleteOperation<U> =
        ValueCompleteOperation(status, maybeValue.map(mapper), builderInit)

    /**
     * Flat map this operation to another.
     *
     * @param U type of the new complete operation
     * @param mapper the mapping function
     */
    fun <U : CompleteOperation> flatMap(mapper: (HttpStatus, Option<T>, ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder) -> U): U =
        mapper(status, maybeValue, builderInit)
}