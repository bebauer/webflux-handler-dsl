package de.bebauer.webflux.handler.dsl

import arrow.core.toOption
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Completes with the specified [HttpStatus] and the body from a [Flux]. Allows additional customization of the response
 * through the builder function.
 *
 * @param status the response status
 * @param flux the [Flux]
 * @param builderInit the builder function
 */
inline fun <reified T> HandlerDsl.complete(
    status: HttpStatus,
    flux: Flux<T>,
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): FinalCompleteOperation =
    complete(FinalCompleteOperation { ServerResponse.status(status).builderInit().body(flux, T::class.java) })

/**
 * Completes with the specified [HttpStatus] and the body from a [Mono]. Allows additional customization of the response
 * through the builder function.
 *
 * @param status the response status
 * @param mono the [Mono]
 * @param builderInit the builder function
 */
inline fun <reified T> HandlerDsl.complete(
    status: HttpStatus,
    mono: Mono<T>,
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): MonoBodyCompleteOperation<T> = complete(MonoBodyCompleteOperation(status, mono, T::class.java, builderInit))

/**
 * Completes the handler with the specified [HttpStatus] and a [ServerResponse.BodyBuilder] result.
 *
 * Example:
 * ```
 * handler {
 *  complete(HttpStatus.BAD_REQUEST) {
 *      contentType(MediaType.APPLICATION_JSON)
 *      body(fromObject("test"))
 *  }
 * }
 * ```
 *
 * @param status the response status
 */
fun HandlerDsl.complete(
    status: HttpStatus,
    init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse>
): FinalCompleteOperation = complete(FinalCompleteOperation { ServerResponse.status(status).init() })

/**
 * Completes with the specified [HttpStatus] and the body from an object. Allows additional customization of the
 * response through the builder function.
 *
 * @param status the response status
 * @param value the object that's written to the body using [fromObject]
 * @param builderInit the builder function
 */
fun <T> HandlerDsl.complete(
    status: HttpStatus,
    value: T?,
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ValueCompleteOperation<T> = complete(ValueCompleteOperation(status, value.toOption(), builderInit))

/**
 * Completes with the specified [HttpStatus].
 *
 * @param status the response status
 */
fun HandlerDsl.complete(status: HttpStatus): FinalCompleteOperation =
    complete(FinalCompleteOperation { ServerResponse.status(status).build() })

/**
 * Completes with specified [HttpStatus] and the body from a [BodyInserter]. Allows additional customization of the
 * response through the builder function.
 *
 * @param status the response status
 * @param inserter the body inserter
 * @param builderInit the builder function
 */
fun HandlerDsl.complete(
    status: HttpStatus,
    inserter: BodyInserter<*, in ServerHttpResponse>,
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): FinalCompleteOperation =
    complete(FinalCompleteOperation { ServerResponse.status(status).builderInit().body(inserter) })