@file:Suppress("UnassignedFluxMonoInstance")

package de.bebauer.webflux.handler.dsl

import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Completes the handler with status [HttpStatus.OK] and the body from a [Flux].
 *
 * @param flux the [Flux]
 */
inline fun <reified T> HandlerDsl.complete(flux: Flux<T>): CompleteOperation = complete {
    body(flux, T::class.java)
}

/**
 * Completes the handler with status [HttpStatus.OK] and the body from a [Mono].
 *
 * @param mono the [Mono]
 */
inline fun <reified T> HandlerDsl.complete(mono: Mono<T>): CompleteOperation =
    complete(mono.flatMap { ServerResponse.ok().body(Mono.just(it), T::class.java) })

/**
 * Completes with the specified [HttpStatus] and the body from a [Flux].
 *
 * @param status the response status
 * @param flux the [Flux]
 */
inline fun <reified T> HandlerDsl.complete(status: HttpStatus, flux: Flux<T>): CompleteOperation = complete(
    status
) {
    body(flux, T::class.java)
}

/**
 * Completes with the specified [HttpStatus] and the body from a [Mono].
 *
 * @param status the response status
 * @param mono the [Mono]
 */
inline fun <reified T> HandlerDsl.complete(status: HttpStatus, mono: Mono<T>): CompleteOperation = complete(
    mono.flatMap { ServerResponse.status(status).body(Mono.just(it), T::class.java) }
)

/**
 * Completes the handler with status [HttpStatus.OK] and a [ServerResponse.BodyBuilder] result.
 *
 * Example:
 * ```
 * handler {
 *  complete {
 *      contentType(MediaType.APPLICATION_JSON)
 *      body(fromObject("test"))
 *  }
 * }
 * ```
 */
fun HandlerDsl.complete(init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse>): CompleteOperation {
    val response = ServerResponse.ok()

    return complete(response.init())
}

/**
 * Completes the handler with status [HttpStatus.OK] and an empty body.
 */
fun HandlerDsl.complete(): CompleteOperation = complete { build() }

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
): CompleteOperation {
    val response = ServerResponse.status(status)

    return complete(response.init())
}

/**
 * Completes the handler with status [HttpStatus.OK] and the body from an object.
 *
 * @param value the object that's used for the body
 */
inline fun <reified T> HandlerDsl.complete(value: T?): CompleteOperation = complete(Mono.justOrEmpty(value))

/**
 * Completes with the specified [HttpStatus] and the body from an object.
 *
 * @param status the response status
 * @param value the object that's written to the body using [fromObject]
 */
inline fun <reified T> HandlerDsl.complete(status: HttpStatus, value: T?): CompleteOperation =
    complete(status, Mono.justOrEmpty(value))

/**
 * Completes with the specified [HttpStatus].
 *
 * @param status the response status
 */
fun HandlerDsl.complete(status: HttpStatus): CompleteOperation = complete(status) { build() }

/**
 * Completes with status [HttpStatus.OK] and the body from a [BodyInserter].
 *
 * @param inserter the body inserter
 */
fun HandlerDsl.complete(inserter: BodyInserter<*, in ServerHttpResponse>): CompleteOperation = complete {
    body(inserter)
}

/**
 * Completes with specified [HttpStatus] and the body from a [BodyInserter].
 *
 * @param status the response status
 * @param inserter the body inserter
 */
fun HandlerDsl.complete(
    status: HttpStatus,
    inserter: BodyInserter<*, in ServerHttpResponse>
): CompleteOperation = complete(status) {
    body(inserter)
}