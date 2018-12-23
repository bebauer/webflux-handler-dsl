package de.bebauer.webflux.handler.dsl

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * Allows creating a webflux handler `(ServerRequest) -> Mono<ServerResponse>` from a Kotlin DSL.
 *
 * Example:
 * ```
 * @Component
 * class MyHandler {
 *  val get = handler {
 *      complete("Hello World.")
 *  }
 * }
 * ```
 *
 * @see HandlerDsl
 */
fun handler(init: HandlerDsl.() -> CompleteOperation): (ServerRequest) -> Mono<ServerResponse> = { request ->
    HandlerDsl(request, init)().response
}

/**
 * Provide a `(ServerRequest) -> Mono<out ServerResponse>` Kotlin DSL in order to be able to write idiomatic Kotlin code.
 */
open class HandlerDsl(
    private val request: ServerRequest,
    private val init: HandlerDsl.() -> CompleteOperation
) : () -> CompleteOperation {

    /**
     * Extract the request.
     */
    fun extractRequest(init: HandlerDsl.(ServerRequest) -> CompleteOperation) = init(request)

    /**
     * Executes a handler DSL and returns it's result without completing.
     */
    fun execute(init: HandlerDsl.() -> CompleteOperation): Mono<ServerResponse> {
        return handler(init)(request)
    }

    override fun invoke(): CompleteOperation = init()
}