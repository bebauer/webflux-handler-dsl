package de.bebauer.webflux.handler.dsl

import arrow.core.toOption
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
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
inline fun <reified T> complete(
    status: HttpStatus,
    flux: Flux<T>,
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ResponseBuilderCompleteOperation =
    ResponseBuilderCompleteOperation { ServerResponse.status(status).builderInit().body(flux, T::class.java) }

/**
 * Completes with the specified [HttpStatus] and the body from a [Mono]. Allows additional customization of the response
 * through the builder function.
 *
 * @param status the response status
 * @param mono the [Mono]
 * @param builderInit the builder function
 */
inline fun <reified T> complete(
    status: HttpStatus,
    mono: Mono<T>,
    noinline builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): MonoBodyCompleteOperation<T> = MonoBodyCompleteOperation(status, mono, T::class.java, builderInit)

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
fun complete(
    status: HttpStatus,
    init: ServerResponse.BodyBuilder.() -> Mono<ServerResponse>
): ResponseBuilderCompleteOperation = ResponseBuilderCompleteOperation { ServerResponse.status(status).init() }

/**
 * Completes with the specified [HttpStatus] and the body from an object. Allows additional customization of the
 * response through the builder function.
 *
 * @param status the response status
 * @param value the object that's written to the body using [fromObject]
 * @param builderInit the builder function
 */
fun <T> complete(
    status: HttpStatus,
    value: T?,
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ValueCompleteOperation<T> = ValueCompleteOperation(status, value.toOption(), builderInit)

/**
 * Completes with the specified [HttpStatus].
 *
 * @param status the response status
 */
fun complete(status: HttpStatus): ResponseBuilderCompleteOperation =
    ResponseBuilderCompleteOperation { ServerResponse.status(status).build() }

/**
 * Completes with specified [HttpStatus] and the body from a [BodyInserter]. Allows additional customization of the
 * response through the builder function.
 *
 * @param status the response status
 * @param inserter the body inserter
 * @param builderInit the builder function
 */
fun complete(
    status: HttpStatus,
    inserter: BodyInserter<*, in ServerHttpResponse>,
    builderInit: ServerResponse.BodyBuilder.() -> ServerResponse.BodyBuilder = { this }
): ResponseBuilderCompleteOperation =
    ResponseBuilderCompleteOperation { ServerResponse.status(status).builderInit().body(inserter) }

/**
 * Complete the handler with complete operation [Mono].
 *
 * @param operation the [CompleteOperation] Mono
 */
fun <T : CompleteOperation> complete(operation: Mono<T>): NestedCompleteOperation<T> {
    return NestedCompleteOperation(operation)
}

/**
 * Builds a complete operation from a complete operation [Mono].
 */
fun <T : CompleteOperation> Mono<T>.toCompleteOperation(): NestedCompleteOperation<T> = complete(this)

/**
 * Complete the handler with the specified response.
 *
 * @param response the [ServerResponse] Mono
 */
fun complete(response: Mono<ServerResponse>): ResponseCompleteOperation {
    return ResponseCompleteOperation(response)
}

/**
 * Fails the handler with the specified exception.
 *
 * @param throwable the exception that caused the failure
 */
fun failWith(throwable: Throwable): ResponseCompleteOperation = complete(Mono.error<ServerResponse>(throwable))

/**
 *  Fails the handler with an Internal Server Error and the specified message.
 *
 *  @param message the error message
 */
fun failWith(message: String): ResponseCompleteOperation =
    failWith(ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message))