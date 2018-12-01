package webflux.handler.dsl

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
fun handler(init: HandlerDsl.() -> Unit): (ServerRequest) -> Mono<out ServerResponse> = {
    val result = HandlerDsl(it, init).invoke()

    when (result) {
        is Either.Left -> throw result.a
        is Either.Right -> result.b
    }
}

/**
 * Provide a `(ServerRequest) -> Mono<out ServerResponse>` Kotlin DSL in order to be able to write idiomatic Kotlin code.
 */
open class HandlerDsl(
    private val request: ServerRequest,
    private val init: HandlerDsl.() -> Unit
) : () -> Either<Throwable, Mono<out ServerResponse>> {

    private var response: Option<Either<Throwable, Mono<out ServerResponse>>> = None
        set(value) {
            field = when (field) {
                is None -> value
                else -> Some(
                    Left(
                        ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Response is already set."
                        )
                    )
                )
            }
        }

    /**
     * Complete the handler with the specified response.
     *
     * @param response the [ServerResponse] Mono.
     */
    fun complete(response: Mono<out ServerResponse>) {
        this.response = Some(Right(response))
    }

    /**
     * Fails the handler with the specified exception.
     *
     * @param throwable the exception that caused the failure
     */
    fun failWith(throwable: Throwable) {
        this.response = Some(Left(throwable))
    }

    /**
     *  Fails the handler with an Internal Server Error and the specified message.
     *
     *  @param message the error message
     */
    fun failWith(message: String) = failWith(ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message))

    /**
     * Extract the request.
     */
    fun extractRequest(init: HandlerDsl.(ServerRequest) -> Unit) {
        init(request)
    }

    override fun invoke(): Either<Throwable, Mono<out ServerResponse>> {
        init()

        return response.getOrElse {
            Left(ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Incomplete DSL."))
        }
    }
}