package de.bebauer.webflux.handler.dsl

import arrow.core.*
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
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
fun <T : CompleteOperation> handler(init: HandlerDsl<T>.() -> T): (ServerRequest) -> Mono<ServerResponse> = { request ->
    Mono.just(HandlerDsl(request, init))
        .flatMap { dsl ->
            dsl.invoke(ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(fromObject("Missing DSL.")))
        }
}

/**
 * Provide a `(ServerRequest) -> Mono<out ServerResponse>` Kotlin DSL in order to be able to write idiomatic Kotlin code.
 */
open class HandlerDsl<T : CompleteOperation>(
    private val request: ServerRequest,
    private val init: HandlerDsl<T>.() -> T
) : () -> T {

    private var completion: Option<CompleteOperation> = None

    fun <T : CompleteOperation> complete(op: T): T {
        completion = completion.map {
            ResponseCompleteOperation(
                Mono.error(
                    ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Response is already set."
                    )
                )
            )
        }.orElse { Some(op) }

        return op
    }

    /**
     * Complete the handler with the specified response.
     *
     * @param response the [ServerResponse] Mono
     */
    fun complete(response: Mono<ServerResponse>): ResponseCompleteOperation {
        return complete(ResponseCompleteOperation(response))
    }

    /**
     * Fails the handler with the specified exception.
     *
     * @param throwable the exception that caused the failure
     */
    fun failWith(throwable: Throwable): CompleteOperation = complete(Mono.error(throwable))

    /**
     *  Fails the handler with an Internal Server Error and the specified message.
     *
     *  @param message the error message
     */
    fun failWith(message: String): CompleteOperation =
        failWith(ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message))

    /**
     * Extract the request.
     */
    fun extractRequest(init: HandlerDsl.(ServerRequest) -> Unit) {
        init(request)
    }

    /**
     * Executes a handler DSL and returns it's result without completing.
     */
    fun execute(init: HandlerDsl.() -> Unit): Mono<ServerResponse> {
        return handler(init)(request)
    }

    override fun invoke(): Mono<ServerResponse> {
        return input
            .map {
                init()
            }
            .flatMap {
                completion
                    .map { c -> c.response }
                    .getOrElse {
                        Mono.error(
                            ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Incomplete DSL."
                            )
                        )
                    }
            }
    }
}