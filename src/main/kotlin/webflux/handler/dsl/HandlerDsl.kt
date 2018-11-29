package webflux.handler.dsl

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

fun handler(init: HandlerDsl.() -> Unit): (ServerRequest) -> Mono<out ServerResponse> = {
    HandlerDsl(it, init).invoke()
}

open class HandlerDsl(val request: ServerRequest,
                      private val init: HandlerDsl.() -> Unit) : () -> Mono<out ServerResponse> {

    private var response: Option<Mono<out ServerResponse>> = None

    fun complete(response: Mono<out ServerResponse>) {
        this.response = Some(response)
    }

    fun nest(init: HandlerDsl.(ServerRequest) -> Unit) {
        response = Some(HandlerDsl(request) { init(request) }.invoke())
    }

    override fun invoke(): Mono<out ServerResponse> {
        init()

        return response.getOrElse {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                          "Incomplete DSL.")
        }
    }
}