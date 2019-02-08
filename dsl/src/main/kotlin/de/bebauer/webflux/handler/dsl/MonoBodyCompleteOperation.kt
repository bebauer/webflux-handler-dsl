package de.bebauer.webflux.handler.dsl

import arrow.core.Option
import arrow.core.getOrElse
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * Complete operation from a [Mono] body.
 *
 * @param T type of the body element
 * @param status the response status
 * @param mono the body [Mono]
 * @param elementClass the class of the body element
 * @param builderInit block for customizing the response
 */
class MonoBodyCompleteOperation<T>(
    val status: HttpStatus,
    val mono: Mono<T>,
    private val elementClass: Class<T>,
    val builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
) : ChainableCompleteOperation() {
    override fun buildResponseWithFallback(maybeFallback: Option<() -> Mono<ServerResponse>>): Mono<ServerResponse> =
        maybeFallback.map { fallback ->
            mono.flatMap {
                ServerResponse.status(status).builderInit().body(Mono.just(it), elementClass)
            }.switchIfEmpty(Mono.defer { fallback() })
        }.getOrElse {
            ServerResponse.status(status).builderInit().body(mono, elementClass)
        }

    /**
     * Map the body [Mono].
     *
     * @param U type of the mapping result
     * @param mapper the mapping function
     */
    inline fun <reified U> map(noinline mapper: (T) -> U) =
        MonoBodyCompleteOperation(status, mono.map(mapper), U::class.java, builderInit)

    /**
     * Flat map this operation to another.
     *
     * @param U type of the new complete operation
     * @param mapper the mapping function
     */
    fun <U : CompleteOperation> flatMap(mapper: (HttpStatus, Mono<T>, ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder) -> U): U =
        mapper(status, mono, builderInit)

    /**
     * Flat map this operation to a [Mono] of another operation.
     *
     * @param U type of the new complete operation
     * @param mapper the mapping function
     */
    fun <U : CompleteOperation> flatMapMono(mapper: (HttpStatus, Mono<T>, ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder) -> Mono<U>)
            : NestedCompleteOperation<U> = NestedCompleteOperation(mapper(status, mono, builderInit))
}