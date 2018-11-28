package webflux.handler.dsl

import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

fun handler(init: HandlerDsl.() -> Unit): (ServerRequest) -> Mono<out ServerResponse> = {
    HandlerDsl(it, init).invoke()
}

typealias Response = (ServerRequest) -> Mono<out ServerResponse>

open class HandlerDsl(
    private val request: ServerRequest,
    private val init: HandlerDsl.() -> Unit
) : () -> Mono<out ServerResponse> {

    var response: Response =
        { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(fromObject("Incomplete DSL")) }

//    fun extractPathVariables(
//        vararg names: String,
//        init: HandlerDsl.(List<String>) -> Unit
//    ) {
//        response = createResponse {
//            init(names.map { request.pathVariable(it) })
//        }
//    }

    fun respond(f: Response) {
        response = f
    }

    fun nest(init: HandlerDsl.(ServerRequest) -> Unit): Response = { request ->
        HandlerDsl(request) {
            init(request)
        }.invoke()
    }

    override fun invoke(): Mono<out ServerResponse> {
        init()

        return response(request)
    }
}