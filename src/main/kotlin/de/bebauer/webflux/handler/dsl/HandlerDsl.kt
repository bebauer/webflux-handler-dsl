package de.bebauer.webflux.handler.dsl

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
fun handler(init: HandlerDsl.() -> Unit): (ServerRequest) -> Mono<ServerResponse> = { request ->
    Mono.just(HandlerDsl(request, init))
        .flatMap { dsl ->
            dsl.invoke(ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(fromObject("Missing DSL.")))
        }
}


class CompleteOperation(internal val response: Mono<ServerResponse>)

/**
 * Provide a `(ServerRequest) -> Mono<out ServerResponse>` Kotlin DSL in order to be able to write idiomatic Kotlin code.
 */
open class HandlerDsl(
    private val request: ServerRequest,
    private val init: HandlerDsl.() -> Unit
) : (Mono<ServerResponse>) -> Mono<ServerResponse> {

    private var completion: Mono<CompleteOperation> = Mono.empty()

    /**
     * Combines two complete operations with <code>or</code>.
     *
     * @param other the other [CompleteOperation]
     */
    infix fun CompleteOperation.or(other: CompleteOperation): CompleteOperation {
        return updateCompleteOperation(CompleteOperation(this.response.switchIfEmpty(other.response)), true)
    }

    private fun updateCompleteOperation(
        completeOperation: CompleteOperation,
        allowReplace: Boolean = false
    ): CompleteOperation {
        completion = completion.map {
            when (allowReplace) {
                true -> completeOperation
                false -> CompleteOperation(
                    Mono.error(
                        ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Response is already set."
                        )
                    )
                )
            }
        }.defaultIfEmpty(completeOperation)

        return completeOperation
    }

    /**
     * Complete the handler with the specified response.
     *
     * @param response the [ServerResponse] Mono
     */
    fun complete(response: Mono<ServerResponse>): CompleteOperation {
        return updateCompleteOperation(CompleteOperation(response))
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

    override fun invoke(input: Mono<ServerResponse>): Mono<ServerResponse> {
        return input
            .map {
                init()
            }
            .flatMap {
                completion
                    .map { c -> c.response }
                    .switchIfEmpty(
                        Mono.error<Mono<ServerResponse>>(
                            ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Incomplete DSL."
                            )
                        )
                    )
            }
            .flatMap { it }
    }
}