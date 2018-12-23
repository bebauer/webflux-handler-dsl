package de.bebauer.webflux.handler.dsl

import arrow.core.Option
import arrow.core.getOrElse
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

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

    inline fun <reified U> map(noinline mapper: (T) -> U) =
        MonoBodyCompleteOperation(status, mono.map(mapper), U::class.java, builderInit)

    fun <U : CompleteOperation> flatMap(mapper: (HttpStatus, Mono<T>, ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder) -> U): U =
        mapper(status, mono, builderInit)
}