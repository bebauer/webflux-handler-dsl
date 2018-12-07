package de.bebauer.webflux.handler.dsl

import arrow.core.*
import org.springframework.http.HttpStatus
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
fun handler(init: HandlerDsl.() -> Unit): (ServerRequest) -> Mono<ServerResponse> = {
    val result = HandlerDsl(it, init).invoke()

    when (result) {
        is Either.Left -> throw result.a
        is Either.Right -> result.b
    }
}

class CompleteOperation(internal val response: Either<Throwable, Mono<ServerResponse>>)

/**
 * Provide a `(ServerRequest) -> Mono<out ServerResponse>` Kotlin DSL in order to be able to write idiomatic Kotlin code.
 */
open class HandlerDsl(
    private val request: ServerRequest,
    private val init: HandlerDsl.() -> Unit
) : () -> Either<Throwable, Mono<out ServerResponse>> {

    private var completion: Option<CompleteOperation> = None

    /**
     * Combines two complete operations with <code>or</code>.
     *
     * @param other the other [CompleteOperation]
     */
    infix fun CompleteOperation.or(other: CompleteOperation): CompleteOperation {
        return updateCompleteOperation(CompleteOperation(this.response.flatMap { left ->
            other.response.map { right ->
                left.switchIfEmpty(right)
            }
        }), true)
    }

    private fun updateCompleteOperation(
        completeOperation: CompleteOperation,
        allowReplace: Boolean = false
    ): CompleteOperation {
        completion = completion.map {
            when (allowReplace) {
                true -> completeOperation
                false -> CompleteOperation(
                    Left(
                        ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Response is already set."
                        )
                    )
                )
            }
        }.orElse { Some(completeOperation) }

        return completeOperation
    }

    /**
     * Complete the handler with the specified response.
     *
     * @param response the [ServerResponse] Mono
     */
    fun complete(response: Mono<ServerResponse>): CompleteOperation {
        return complete(Right(response))
    }

    /**
     * Complete the handler with the specified either an exception or the response.
     *
     * @param result with which to complete
     */
    fun complete(result: Either<Throwable, Mono<ServerResponse>>): CompleteOperation {
        return updateCompleteOperation(CompleteOperation(result))
    }

    /**
     * Fails the handler with the specified exception.
     *
     * @param throwable the exception that caused the failure
     */
    fun failWith(throwable: Throwable): CompleteOperation = complete(Left(throwable))

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
    fun execute(init: HandlerDsl.() -> Unit): Either<Throwable, Mono<ServerResponse>> {
        val dsl = HandlerDsl(request, init)

        return Try { dsl.invoke() }.toEither().flatMap { it }
    }

    override fun invoke(): Either<Throwable, Mono<ServerResponse>> {
        init()

        return completion.map { it.response }.getOrElse {
            Left(ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Incomplete DSL."))
        }
    }
}