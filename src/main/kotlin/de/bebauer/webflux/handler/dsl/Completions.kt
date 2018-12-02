package de.bebauer.webflux.handler.dsl

import org.reactivestreams.Publisher
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * Completes the handler with status [HttpStatus.OK] and the body from a publisher.
 *
 * @param publisher the [Publisher]
 */
inline fun <reified T> HandlerDsl.complete(publisher: Publisher<T>) = complete {
    body(publisher, T::class.java)
}

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
fun HandlerDsl.complete(init: ServerResponse.BodyBuilder.() -> Mono<out ServerResponse>) {
    val response = ServerResponse.ok()

    complete(response.init())
}

/**
 * Completes the handler with status [HttpStatus.OK] and an empty body.
 */
fun HandlerDsl.complete() = complete { build() }

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
    init: ServerResponse.BodyBuilder.() -> Mono<out ServerResponse>
) {
    val response = ServerResponse.status(status)

    complete(response.init())
}

/**
 * Completes the handler with status [HttpStatus.OK] and the body from an object.
 *
 * @param value the object that's written to the body using [fromObject]
 */
fun <T> HandlerDsl.complete(value: T) = complete { body(fromObject(value)) }

/**
 * Completes with the specified [HttpStatus].
 *
 * @param status the response status
 */
fun HandlerDsl.complete(status: HttpStatus) = complete(status) { build() }

/**
 * Completes with the specified [HttpStatus] and the body from an object.
 *
 * @param status the response status
 * @param value the object that's written to the body using [fromObject]
 */
fun <T> HandlerDsl.complete(status: HttpStatus, value: T) = complete(status) {
    body(fromObject(value))
}

/**
 * Completes with the specified [HttpStatus] and the body from a publisher.
 *
 * @param status the response status
 * @param publisher the [Publisher]
 */
inline fun <reified T> HandlerDsl.complete(status: HttpStatus, publisher: Publisher<T>) = complete(
    status
) {
    body(publisher, T::class.java)
}

/**
 * Completes with status [HttpStatus.OK] and the body from a [BodyInserter].
 *
 * @param inserter the body inserter
 */
fun HandlerDsl.complete(inserter: BodyInserter<*, in ServerHttpResponse>) = complete {
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
) = complete(status) {
    body(inserter)
}